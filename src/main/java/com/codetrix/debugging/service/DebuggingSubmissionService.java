package com.codetrix.debugging.service;

import com.codetrix.coding.entity.SubmissionStatus;
import com.codetrix.coding.service.CodeExecutionService;
import com.codetrix.debugging.dto.*;
import com.codetrix.debugging.entity.DebuggingProblem;
import com.codetrix.debugging.entity.DebuggingSubmission;
import com.codetrix.debugging.entity.DebuggingTestCase;
import com.codetrix.debugging.exception.DebuggingException;
import com.codetrix.debugging.repository.DebuggingSubmissionRepository;
import com.codetrix.event.entity.RoundType;
import com.codetrix.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebuggingSubmissionService {

    private final DebuggingSubmissionRepository submissionRepository;
    private final DebuggingProblemService problemService;
    private final CodeExecutionService codeExecutionService;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public DebuggingRunResponse runCode(Long teamId, DebuggingRunRequest request) {
        validateDebuggingRoundActive();

        DebuggingProblem problem = problemService.getProblemEntity(request.getProblemId());

        CodeExecutionService.ExecutionResult result = codeExecutionService.runCode(
                request.getSourceCode(),
                problem.getLanguage(),
                request.getCustomInput(),
                problem.getTimeLimitMs(),
                problem.getMemoryLimitMb()
        );

        log.debug("Run code for team {} on debugging problem {}: {}", teamId, request.getProblemId(), result.status());

        return DebuggingRunResponse.builder()
                .success(result.success())
                .status(result.status())
                .output(result.output())
                .error(result.error())
                .compileOutput(result.compileOutput())
                .executionTimeMs(result.executionTimeMs())
                .memoryUsedKb(result.memoryUsedKb())
                .build();
    }

    @Transactional
    public DebuggingSubmissionResponse submitCode(Long teamId, String teamCode, DebuggingSubmitRequest request) {
        validateDebuggingRoundActive();

        DebuggingProblem problem = problemService.getProblemEntity(request.getProblemId());

        DebuggingSubmission submission = DebuggingSubmission.builder()
                .teamId(teamId)
                .teamCode(teamCode)
                .problemId(problem.getId())
                .language(problem.getLanguage())
                .sourceCode(request.getSourceCode())
                .submissionTime(Instant.now())
                .status(SubmissionStatus.QUEUED)
                .totalTests(problem.getTestCases().size())
                .build();

        submission = submissionRepository.save(submission);
        log.info("Created debugging submission {} for team {} on problem {}",
                submission.getId(), teamCode, problem.getTitle());

        judgeSubmissionAsync(submission, problem);

        return DebuggingSubmissionResponse.fromEntityWithProblem(submission, problem.getTitle(), problem.getPoints());
    }

    @Async
    @Transactional
    public void judgeSubmissionAsync(DebuggingSubmission submission, DebuggingProblem problem) {
        try {
            submission.setStatus(SubmissionStatus.RUNNING);
            submissionRepository.save(submission);

            List<DebuggingTestCase> testCases = problem.getTestCases();

            List<com.codetrix.coding.entity.TestCase> codingTestCases = testCases.stream()
                    .map(tc -> com.codetrix.coding.entity.TestCase.builder()
                            .id(tc.getId())
                            .input(tc.getInput())
                            .expectedOutput(tc.getExpectedOutput())
                            .isSample(tc.getIsSample())
                            .displayOrder(tc.getDisplayOrder())
                            .build())
                    .toList();

            com.codetrix.coding.entity.Submission codingSubmission = com.codetrix.coding.entity.Submission.builder()
                    .id(submission.getId())
                    .teamId(submission.getTeamId())
                    .teamCode(submission.getTeamCode())
                    .problemId(submission.getProblemId())
                    .language(submission.getLanguage())
                    .sourceCode(submission.getSourceCode())
                    .submissionTime(submission.getSubmissionTime())
                    .build();

            CodeExecutionService.JudgeResult result = codeExecutionService.judgeSubmission(
                    codingSubmission,
                    codingTestCases,
                    problem.getTimeLimitMs(),
                    problem.getMemoryLimitMb()
            );

            int pointsEarned = 0;
            SubmissionStatus status;

            switch (result.status()) {
                case "ACCEPTED" -> {
                    status = SubmissionStatus.ACCEPTED;
                    pointsEarned = problem.getPoints();
                }
                case "PARTIAL" -> {
                    status = SubmissionStatus.PARTIAL;
                    pointsEarned = (int) ((double) result.testsPassed() / result.totalTests() * problem.getPoints());
                }
                case "WRONG_ANSWER" -> status = SubmissionStatus.WRONG_ANSWER;
                case "COMPILE_ERROR" -> status = SubmissionStatus.COMPILE_ERROR;
                case "RUNTIME_ERROR" -> status = SubmissionStatus.RUNTIME_ERROR;
                case "TIME_LIMIT" -> status = SubmissionStatus.TIME_LIMIT;
                default -> status = SubmissionStatus.WRONG_ANSWER;
            }

            submission.setStatus(status);
            submission.setTestsPassed(result.testsPassed());
            submission.setTotalTests(result.totalTests());
            submission.setPointsEarned(pointsEarned);
            submission.setExecutionTimeMs(result.executionTimeMs());
            submission.setMemoryUsedKb(result.memoryUsedKb());
            submission.setCompileOutput(result.compileOutput());
            submission.setErrorMessage(result.errorMessage());

            submissionRepository.save(submission);
            log.info("Judged debugging submission {}: {} ({}/{} tests, {} points)",
                    submission.getId(), status, result.testsPassed(), result.totalTests(), pointsEarned);

        } catch (Exception e) {
            log.error("Error judging debugging submission {}: {}", submission.getId(), e.getMessage(), e);
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submission.setErrorMessage("Internal judging error: " + e.getMessage());
            submissionRepository.save(submission);
        }
    }

    @Transactional(readOnly = true)
    public DebuggingSubmissionListResponse getSubmissionsForTeam(Long teamId) {
        List<DebuggingSubmission> submissions = submissionRepository.findByTeamIdOrderBySubmissionTimeDesc(teamId);

        Map<Long, DebuggingProblem> problemMap = submissions.stream()
                .map(DebuggingSubmission::getProblemId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> problemService.getProblemEntity(id)
                ));

        List<DebuggingSubmissionResponse> responses = submissions.stream()
                .map(s -> {
                    DebuggingProblem p = problemMap.get(s.getProblemId());
                    return DebuggingSubmissionResponse.fromEntityWithProblem(s, p.getTitle(), p.getPoints());
                })
                .toList();

        List<Long> solvedProblems = submissionRepository.findSolvedProblemIds(teamId);
        Integer totalPoints = submissionRepository.calculateTotalPoints(teamId);

        return DebuggingSubmissionListResponse.builder()
                .submissions(responses)
                .totalSubmissions(submissions.size())
                .totalPointsEarned(totalPoints != null ? totalPoints : 0)
                .problemsSolved(solvedProblems.size())
                .build();
    }

    @Transactional(readOnly = true)
    public DebuggingSubmissionResponse getSubmission(Long submissionId, Long teamId) {
        DebuggingSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> DebuggingException.submissionNotFound(submissionId));

        if (!submission.getTeamId().equals(teamId)) {
            throw DebuggingException.submissionNotFound(submissionId);
        }

        DebuggingProblem problem = problemService.getProblemEntity(submission.getProblemId());
        return DebuggingSubmissionResponse.fromEntityWithProblem(submission, problem.getTitle(), problem.getPoints());
    }

    private void validateDebuggingRoundActive() {
        if (!eventService.isSubmissionAllowed(RoundType.DEBUGGING)) {
            throw DebuggingException.roundNotActive();
        }
    }
}
