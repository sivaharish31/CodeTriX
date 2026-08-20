package com.codetrix.debugging.service;

import com.codetrix.coding.entity.Language;
import com.codetrix.debugging.dto.*;
import com.codetrix.debugging.entity.DebuggingProblem;
import com.codetrix.debugging.entity.DebuggingTestCase;
import com.codetrix.debugging.exception.DebuggingException;
import com.codetrix.debugging.repository.DebuggingProblemRepository;
import com.codetrix.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DebuggingProblemService {

    private final DebuggingProblemRepository problemRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<DebuggingProblemResponse> getAllProblemsForStudent() {
        List<DebuggingProblem> problems = problemRepository.findByEnabledTrueOrderByDisplayOrderAsc();
        return problems.stream()
                .map(DebuggingProblemResponse::listView)
                .toList();
    }

    @Transactional(readOnly = true)
    public DebuggingProblemResponse getProblemForStudent(Long id) {
        DebuggingProblem problem = problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> DebuggingException.problemNotFound(id));

        if (!problem.getEnabled()) {
            throw DebuggingException.problemNotFound(id);
        }

        return DebuggingProblemResponse.fromEntity(problem, true);
    }

    @Transactional(readOnly = true)
    public List<DebuggingProblemAdminResponse> getAllProblemsForAdmin() {
        List<DebuggingProblem> problems = problemRepository.findAllWithTestCases();
        return problems.stream()
                .map(DebuggingProblemAdminResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DebuggingProblemAdminResponse getProblemForAdmin(Long id) {
        DebuggingProblem problem = problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> DebuggingException.problemNotFound(id));
        return DebuggingProblemAdminResponse.fromEntity(problem);
    }

    @Transactional
    public DebuggingProblemAdminResponse createProblem(CreateDebuggingProblemRequest request) {
        validateEventNotStarted();

        if (problemRepository.existsByTitle(request.getTitle())) {
            throw DebuggingException.duplicateTitle(request.getTitle());
        }

        Language language = Language.fromString(request.getLanguage());

        DebuggingProblem problem = DebuggingProblem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .buggyCode(request.getBuggyCode())
                .language(language)
                .points(request.getPoints())
                .timeLimitMs(request.getTimeLimitMs() != null ? request.getTimeLimitMs() : 2000)
                .memoryLimitMb(request.getMemoryLimitMb() != null ? request.getMemoryLimitMb() : 256)
                .hint(request.getHint())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .enabled(true)
                .testCases(new ArrayList<>())
                .build();

        if (request.getTestCases() != null) {
            for (DebuggingTestCaseRequest tcReq : request.getTestCases()) {
                DebuggingTestCase testCase = createTestCase(tcReq);
                problem.addTestCase(testCase);
            }
        }

        problem = problemRepository.save(problem);
        log.info("Created debugging problem: {} with {} test cases", problem.getTitle(), problem.getTestCases().size());

        return DebuggingProblemAdminResponse.fromEntity(problem);
    }

    @Transactional
    public DebuggingProblemAdminResponse updateProblem(Long id, UpdateDebuggingProblemRequest request) {
        validateEventNotStarted();

        DebuggingProblem problem = problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> DebuggingException.problemNotFound(id));

        if (request.getTitle() != null && !request.getTitle().equals(problem.getTitle())) {
            if (problemRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
                throw DebuggingException.duplicateTitle(request.getTitle());
            }
            problem.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) problem.setDescription(request.getDescription());
        if (request.getBuggyCode() != null) problem.setBuggyCode(request.getBuggyCode());
        if (request.getLanguage() != null) problem.setLanguage(Language.fromString(request.getLanguage()));
        if (request.getPoints() != null) problem.setPoints(request.getPoints());
        if (request.getTimeLimitMs() != null) problem.setTimeLimitMs(request.getTimeLimitMs());
        if (request.getMemoryLimitMb() != null) problem.setMemoryLimitMb(request.getMemoryLimitMb());
        if (request.getHint() != null) problem.setHint(request.getHint());
        if (request.getDisplayOrder() != null) problem.setDisplayOrder(request.getDisplayOrder());
        if (request.getEnabled() != null) problem.setEnabled(request.getEnabled());

        if (request.getTestCases() != null) {
            problem.getTestCases().clear();
            for (DebuggingTestCaseRequest tcReq : request.getTestCases()) {
                DebuggingTestCase testCase = createTestCase(tcReq);
                problem.addTestCase(testCase);
            }
        }

        problem = problemRepository.save(problem);
        log.info("Updated debugging problem: {}", problem.getTitle());

        return DebuggingProblemAdminResponse.fromEntity(problem);
    }

    @Transactional
    public void deleteProblem(Long id) {
        validateEventNotStarted();

        DebuggingProblem problem = problemRepository.findById(id)
                .orElseThrow(() -> DebuggingException.problemNotFound(id));

        problemRepository.delete(problem);
        log.info("Deleted debugging problem: {}", problem.getTitle());
    }

    private DebuggingTestCase createTestCase(DebuggingTestCaseRequest request) {
        return DebuggingTestCase.builder()
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .isSample(request.getIsSample() != null ? request.getIsSample() : false)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .explanation(request.getExplanation())
                .build();
    }

    private void validateEventNotStarted() {
        if (eventRepository.hasEventStartedOrCompleted()) {
            throw DebuggingException.cannotModifyAfterStart();
        }
    }

    @Transactional(readOnly = true)
    public DebuggingProblem getProblemEntity(Long id) {
        return problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> DebuggingException.problemNotFound(id));
    }
}
