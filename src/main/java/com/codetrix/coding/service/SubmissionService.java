package com.codetrix.coding.service;

import com.codetrix.coding.dto.*;
import com.codetrix.coding.entity.*;
import com.codetrix.coding.exception.CodingException;
import com.codetrix.coding.repository.SubmissionRepository;
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
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final CodingProblemService problemService;
    private final CodeExecutionService codeExecutionService;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public RunCodeResponse runCode(Long teamId, RunCodeRequest request) {
        validateCodingRoundActive();

        CodingProblem problem = problemService.getProblemEntity(request.getProblemId());
        Language language = parseLanguage(request.getLanguage());

        CodeExecutionService.ExecutionResult result = codeExecutionService.runCode(
                request.getSourceCode(),
                language,
                request.getCustomInput(),
                problem.getTimeLimitMs(),
                problem.getMemoryLimitMb()
        );

        log.debug("Run code for team {} on problem {}: {}", teamId, request.getProblemId(), result.status());

        return RunCodeResponse.builder()
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
    public SubmissionResponse submitCode(Long teamId, String teamCode, SubmitCodeRequest request) {
        validateCodingRoundActive();

        CodingProblem problem = problemService.getProblemEntity(request.getProblemId());
        Language language = parseLanguage(request.getLanguage());

        Submission submission = Submission.builder()
                .teamId(teamId)
                .teamCode(teamCode)
                .problemId(problem.getId())
                .language(language)
                .sourceCode(request.getSourceCode())
                .submissionTime(Instant.now())
                .status(SubmissionStatus.QUEUED)
                .totalTests(problem.getTestCases().size())
                .build();

        submission = submissionRepository.save(submission);
        log.info("Created submission {} for team {} on problem {}", submission.getId(), teamCode, problem.getTitle());

        judgeSubmissionAsync(submission, problem);

        return SubmissionResponse.fromEntityWithProblem(submission, problem.getTitle(), problem.getPoints());
    }

    @Async
    @Transactional
    public void judgeSubmissionAsync(Submission submission, CodingProblem problem) {
        try {
            submission.setStatus(SubmissionStatus.RUNNING);
            submissionRepository.save(submission);

            List<TestCase> testCases = problem.getTestCases();

            CodeExecutionService.JudgeResult result = codeExecutionService.judgeSubmission(
                    submission,
                    testCases,
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
            log.info("Judged submission {}: {} ({}/{} tests, {} points)",
                    submission.getId(), status, result.testsPassed(), result.totalTests(), pointsEarned);

        } catch (Exception e) {
            log.error("Error judging submission {}: {}", submission.getId(), e.getMessage(), e);
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submission.setErrorMessage("Internal judging error: " + e.getMessage());
            submissionRepository.save(submission);
        }
    }

    @Transactional(readOnly = true)
    public SubmissionListResponse getSubmissionsForTeam(Long teamId) {
        List<Submission> submissions = submissionRepository.findByTeamIdOrderBySubmissionTimeDesc(teamId);

        Map<Long, CodingProblem> problemMap = submissions.stream()
                .map(Submission::getProblemId)
                .distinct()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> problemService.getProblemEntity(id)
                ));

        List<SubmissionResponse> responses = submissions.stream()
                .map(s -> {
                    CodingProblem p = problemMap.get(s.getProblemId());
                    return SubmissionResponse.fromEntityWithProblem(s, p.getTitle(), p.getPoints());
                })
                .toList();

        List<Long> solvedProblems = submissionRepository.findSolvedProblemIds(teamId);
        Integer totalPoints = submissionRepository.calculateTotalPoints(teamId);

        return SubmissionListResponse.builder()
                .submissions(responses)
                .totalSubmissions(submissions.size())
                .totalPointsEarned(totalPoints != null ? totalPoints : 0)
                .problemsSolved(solvedProblems.size())
                .build();
    }

    @Transactional(readOnly = true)
    public SubmissionResponse getSubmission(Long submissionId, Long teamId) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> CodingException.submissionNotFound(submissionId));

        if (!submission.getTeamId().equals(teamId)) {
            throw CodingException.submissionNotFound(submissionId);
        }

        CodingProblem problem = problemService.getProblemEntity(submission.getProblemId());
        return SubmissionResponse.fromEntityWithProblem(submission, problem.getTitle(), problem.getPoints());
    }

    private void validateCodingRoundActive() {
        if (!eventService.isSubmissionAllowed(RoundType.CODING)) {
            throw CodingException.roundNotActive();
        }
    }

    private Language parseLanguage(String lang) {
        try {
            return Language.fromString(lang);
        } catch (IllegalArgumentException e) {
            throw CodingException.unsupportedLanguage(lang);
        }
    }
}
