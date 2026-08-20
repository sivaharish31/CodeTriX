package com.codetrix.execution.service;

import com.codetrix.execution.config.ExecutionConfig;
import com.codetrix.execution.docker.*;
import com.codetrix.execution.dto.*;
import com.codetrix.execution.entity.ExecutionLanguage;
import com.codetrix.execution.entity.ExecutionStatus;
import com.codetrix.execution.exception.ExecutionException;
import com.codetrix.execution.queue.ExecutionQueueManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerExecutionService {

    private final ExecutionConfig config;
    private final DockerCommandBuilder commandBuilder;
    private final ProcessRunner processRunner;
    private final WorkspaceManager workspaceManager;
    private final OutputComparator outputComparator;
    private final ExecutionQueueManager queueManager;

    @PostConstruct
    public void init() {
        queueManager.setExecutor(this::executeInternal);
        log.info("Docker execution service initialized");
    }

    public CompletableFuture<ExecutionResult> execute(ExecutionRequest request) {
        validateRequest(request);
        return queueManager.submit(request);
    }

    public ExecutionResult executeSync(ExecutionRequest request) {
        try {
            return execute(request).get(
                request.getTimeLimitMs() * (request.getTestCases() != null ? request.getTestCases().size() : 1) + 60000,
                TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            log.error("Execution failed: {}", e.getMessage());
            return ExecutionResult.internalError(e.getMessage());
        }
    }

    private ExecutionResult executeInternal(ExecutionRequest request) {
        WorkspaceManager.Workspace workspace = null;

        try {
            workspace = workspaceManager.createWorkspace(request.getLanguage(), request.getSourceCode());

            if (request.getLanguage().requiresCompilation()) {
                ProcessResult compileResult = compile(request.getLanguage(), workspace.getWorkDir());

                if (!compileResult.isSuccess()) {
                    return ExecutionResult.compilationError(
                        compileResult.getError().isEmpty() ? compileResult.getOutput() : compileResult.getError()
                    );
                }
            }

            if (Boolean.TRUE.equals(request.getCustomRun())) {
                return executeCustomRun(request, workspace);
            } else {
                return executeJudge(request, workspace);
            }

        } catch (IOException e) {
            log.error("Workspace creation failed: {}", e.getMessage());
            return ExecutionResult.internalError("Failed to create execution workspace");
        } finally {
            if (workspace != null) {
                workspaceManager.cleanup(workspace);
            }
        }
    }

    private ProcessResult compile(ExecutionLanguage language, Path workDir) {
        ContainerConfig containerConfig = commandBuilder.createConfig(
            language, workDir, config.getCompilationTimeoutMs(), config.getDefaultMemoryLimitMb()
        );

        List<String> dockerCmd = commandBuilder.buildRunCommand(containerConfig);
        dockerCmd.add("/bin/sh");
        dockerCmd.add("-c");
        dockerCmd.add(language.getCompileCommand());

        log.debug("Compiling with: {}", String.join(" ", dockerCmd));

        ProcessResult result = processRunner.runSimple(dockerCmd, config.getCompilationTimeoutMs());

        forceRemoveContainer(containerConfig.getContainerId());

        return result;
    }

    private ExecutionResult executeCustomRun(ExecutionRequest request, WorkspaceManager.Workspace workspace) {
        try {
            workspaceManager.writeInputFile(workspace.getWorkDir(), request.getCustomInput(), "input.txt");
        } catch (IOException e) {
            return ExecutionResult.internalError("Failed to write input file");
        }

        ProcessResult runResult = runCode(
            request.getLanguage(),
            workspace.getWorkDir(),
            request.getCustomInput(),
            request.getTimeLimitMs(),
            request.getMemoryLimitMb()
        );

        ExecutionStatus status;
        if (runResult.isTimedOut()) {
            status = ExecutionStatus.TIME_LIMIT_EXCEEDED;
        } else if (runResult.getExitCode() != 0) {
            status = ExecutionStatus.RUNTIME_ERROR;
        } else {
            status = ExecutionStatus.ACCEPTED;
        }

        return ExecutionResult.customRunResult(
            status,
            runResult.getOutput(),
            runResult.getError(),
            runResult.getExecutionTimeMs()
        );
    }

    private ExecutionResult executeJudge(ExecutionRequest request, WorkspaceManager.Workspace workspace) {
        List<TestCaseInput> testCases = request.getTestCases();
        if (testCases == null || testCases.isEmpty()) {
            return ExecutionResult.internalError("No test cases provided");
        }

        List<TestCaseResult> results = new ArrayList<>();
        int passedCount = 0;
        long totalTimeMs = 0;
        long maxMemoryKb = 0;
        ExecutionStatus overallStatus = ExecutionStatus.ACCEPTED;

        for (int i = 0; i < testCases.size(); i++) {
            TestCaseInput testCase = testCases.get(i);

            int timeLimit = testCase.getTimeLimitMs() != null
                ? testCase.getTimeLimitMs()
                : request.getTimeLimitMs();
            int memoryLimit = testCase.getMemoryLimitMb() != null
                ? testCase.getMemoryLimitMb()
                : request.getMemoryLimitMb();

            ProcessResult runResult = runCode(
                request.getLanguage(),
                workspace.getWorkDir(),
                testCase.getInput(),
                timeLimit,
                memoryLimit
            );

            TestCaseResult tcResult = evaluateTestCase(i, testCase, runResult);
            results.add(tcResult);

            totalTimeMs += runResult.getExecutionTimeMs();
            if (runResult.getMemoryUsedKb() != null) {
                maxMemoryKb = Math.max(maxMemoryKb, runResult.getMemoryUsedKb());
            }

            if (Boolean.TRUE.equals(tcResult.getPassed())) {
                passedCount++;
            } else if (overallStatus == ExecutionStatus.ACCEPTED) {
                overallStatus = tcResult.getStatus();
            }
        }

        if (passedCount == testCases.size()) {
            overallStatus = ExecutionStatus.ACCEPTED;
        } else if (passedCount > 0) {
            overallStatus = ExecutionStatus.PARTIAL;
        }

        return ExecutionResult.builder()
            .status(overallStatus)
            .passedTests(passedCount)
            .totalTests(testCases.size())
            .executionTimeMs(totalTimeMs)
            .memoryUsedKb(maxMemoryKb > 0 ? maxMemoryKb : null)
            .testCaseResults(results)
            .build();
    }

    private ProcessResult runCode(ExecutionLanguage language, Path workDir, String input, int timeLimit, int memoryLimit) {
        ContainerConfig containerConfig = commandBuilder.createConfig(language, workDir, timeLimit, memoryLimit);

        List<String> dockerCmd = new ArrayList<>(commandBuilder.buildRunCommand(containerConfig));

        dockerCmd.add("/bin/sh");
        dockerCmd.add("-c");

        String runCommand = language.getRunCommand();
        if (language.requiresCompilation()) {
            dockerCmd.add("cd /tmp && cp /code/* . 2>/dev/null || true && " + runCommand);
        } else {
            dockerCmd.add(runCommand);
        }

        log.debug("Running with timeout {}ms, memory {}MB", timeLimit, memoryLimit);

        ProcessResult result = processRunner.run(
            dockerCmd,
            input,
            timeLimit + 1000,
            config.getSecurity().getMaxOutputSizeKb() * 1024
        );

        forceRemoveContainer(containerConfig.getContainerId());

        return result;
    }

    private TestCaseResult evaluateTestCase(int index, TestCaseInput testCase, ProcessResult runResult) {
        ExecutionStatus status;
        boolean passed = false;

        if (runResult.isTimedOut()) {
            status = ExecutionStatus.TIME_LIMIT_EXCEEDED;
        } else if (runResult.getExitCode() != 0) {
            status = ExecutionStatus.RUNTIME_ERROR;
        } else {
            boolean matches = outputComparator.compare(testCase.getExpectedOutput(), runResult.getOutput());
            if (matches) {
                status = ExecutionStatus.ACCEPTED;
                passed = true;
            } else {
                status = ExecutionStatus.WRONG_ANSWER;
            }
        }

        return TestCaseResult.builder()
            .testCaseIndex(index)
            .status(status)
            .passed(passed)
            .executionTimeMs(runResult.getExecutionTimeMs())
            .memoryUsedKb(runResult.getMemoryUsedKb())
            .actualOutput(passed ? null : truncateOutput(runResult.getOutput()))
            .errorOutput(runResult.getError().isEmpty() ? null : truncateOutput(runResult.getError()))
            .build();
    }

    private void forceRemoveContainer(String containerId) {
        try {
            processRunner.runSimple(commandBuilder.buildRemoveCommand(containerId), 5000);
        } catch (Exception e) {
            log.debug("Container {} removal: {}", containerId, e.getMessage());
        }
    }

    private void validateRequest(ExecutionRequest request) {
        if (request.getLanguage() == null) {
            throw ExecutionException.unsupportedLanguage("null");
        }

        int maxSourceSize = config.getSecurity().getMaxSourceCodeSizeKb() * 1024;
        if (request.getSourceCode() != null && request.getSourceCode().length() > maxSourceSize) {
            throw new ExecutionException("Source code exceeds maximum size", "SOURCE_TOO_LARGE");
        }

        if (request.getTimeLimitMs() > config.getMaxTimeLimitMs()) {
            request.setTimeLimitMs(config.getMaxTimeLimitMs());
        }
        if (request.getMemoryLimitMb() > config.getMaxMemoryLimitMb()) {
            request.setMemoryLimitMb(config.getMaxMemoryLimitMb());
        }
    }

    private String truncateOutput(String output) {
        if (output == null) return null;
        int maxLen = config.getSecurity().getMaxOutputSizeKb() * 1024;
        if (output.length() > maxLen) {
            return output.substring(0, maxLen) + "\n... (output truncated)";
        }
        return output;
    }

    public boolean isDockerAvailable() {
        try {
            ProcessResult result = processRunner.runSimple(List.of("docker", "version"), 5000);
            return result.isSuccess();
        } catch (Exception e) {
            return false;
        }
    }

    public ExecutionQueueStatus getQueueStatus() {
        return ExecutionQueueStatus.builder()
            .queueSize(queueManager.getQueueSize())
            .activeExecutions(queueManager.getActiveExecutions())
            .maxConcurrent(config.getMaxConcurrentExecutions())
            .healthy(queueManager.isHealthy())
            .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class ExecutionQueueStatus {
        private int queueSize;
        private int activeExecutions;
        private int maxConcurrent;
        private boolean healthy;
    }
}
