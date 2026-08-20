package com.codetrix.execution.service;

import com.codetrix.coding.entity.Language;
import com.codetrix.coding.entity.Submission;
import com.codetrix.coding.entity.TestCase;
import com.codetrix.coding.service.CodeExecutionService;
import com.codetrix.coding.service.ExecutionResult;
import com.codetrix.coding.service.JudgeResult;
import com.codetrix.execution.dto.ExecutionRequest;
import com.codetrix.execution.dto.TestCaseInput;
import com.codetrix.execution.entity.ExecutionLanguage;
import com.codetrix.execution.entity.ExecutionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CodeExecutionAdapter implements CodeExecutionService {

    private final DockerExecutionService dockerExecutionService;

    @Override
    public ExecutionResult runCode(String sourceCode, Language language, String input, int timeLimitMs, int memoryLimitMb) {
        ExecutionRequest request = ExecutionRequest.builder()
            .language(mapLanguage(language))
            .sourceCode(sourceCode)
            .customRun(true)
            .customInput(input)
            .timeLimitMs(timeLimitMs)
            .memoryLimitMb(memoryLimitMb)
            .build();

        com.codetrix.execution.dto.ExecutionResult result = dockerExecutionService.executeSync(request);

        return new ExecutionResult(
            result.getStatus() == ExecutionStatus.ACCEPTED,
            mapStatus(result.getStatus()),
            result.getRuntimeOutput(),
            result.getErrorOutput(),
            result.getCompilerOutput(),
            result.getExecutionTimeMs() != null ? result.getExecutionTimeMs().intValue() : null,
            result.getMemoryUsedKb() != null ? result.getMemoryUsedKb().intValue() : null
        );
    }

    @Override
    public JudgeResult judgeSubmission(Submission submission, List<TestCase> testCases, int timeLimitMs, int memoryLimitMb) {
        List<TestCaseInput> inputs = testCases.stream()
            .map(tc -> TestCaseInput.builder()
                .input(tc.getInput())
                .expectedOutput(tc.getExpectedOutput())
                .timeLimitMs(timeLimitMs)
                .memoryLimitMb(memoryLimitMb)
                .build())
            .collect(Collectors.toList());

        ExecutionRequest request = ExecutionRequest.builder()
            .language(mapLanguage(submission.getLanguage()))
            .sourceCode(submission.getSourceCode())
            .testCases(inputs)
            .timeLimitMs(timeLimitMs)
            .memoryLimitMb(memoryLimitMb)
            .customRun(false)
            .submissionId(submission.getId().toString())
            .build();

        com.codetrix.execution.dto.ExecutionResult result = dockerExecutionService.executeSync(request);

        return new JudgeResult(
            mapStatus(result.getStatus()),
            result.getPassedTests() != null ? result.getPassedTests() : 0,
            result.getTotalTests() != null ? result.getTotalTests() : testCases.size(),
            0,
            result.getExecutionTimeMs() != null ? result.getExecutionTimeMs().intValue() : null,
            result.getMemoryUsedKb() != null ? result.getMemoryUsedKb().intValue() : null,
            result.getCompilerOutput(),
            result.getErrorOutput()
        );
    }

    private ExecutionLanguage mapLanguage(Language language) {
        return switch (language) {
            case C -> ExecutionLanguage.C;
            case CPP -> ExecutionLanguage.CPP;
            case JAVA -> ExecutionLanguage.JAVA;
            case PYTHON -> ExecutionLanguage.PYTHON;
        };
    }

    private String mapStatus(ExecutionStatus status) {
        return switch (status) {
            case ACCEPTED -> "ACCEPTED";
            case WRONG_ANSWER -> "WRONG_ANSWER";
            case PARTIAL -> "PARTIAL";
            case COMPILATION_ERROR -> "COMPILATION_ERROR";
            case RUNTIME_ERROR -> "RUNTIME_ERROR";
            case TIME_LIMIT_EXCEEDED -> "TIME_LIMIT_EXCEEDED";
            case MEMORY_LIMIT_EXCEEDED -> "MEMORY_LIMIT_EXCEEDED";
            case INTERNAL_ERROR -> "INTERNAL_ERROR";
            default -> "UNKNOWN";
        };
    }
}
