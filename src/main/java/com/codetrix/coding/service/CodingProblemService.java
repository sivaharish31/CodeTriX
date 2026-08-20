package com.codetrix.coding.service;

import com.codetrix.coding.dto.*;
import com.codetrix.coding.entity.CodingProblem;
import com.codetrix.coding.entity.TestCase;
import com.codetrix.coding.exception.CodingException;
import com.codetrix.coding.repository.CodingProblemRepository;
import com.codetrix.coding.repository.TestCaseRepository;
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
public class CodingProblemService {

    private final CodingProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final EventRepository eventRepository;

    @Transactional(readOnly = true)
    public List<ProblemResponse> getAllProblemsForStudent() {
        List<CodingProblem> problems = problemRepository.findByEnabledTrueOrderByDisplayOrderAsc();
        return problems.stream()
                .map(ProblemResponse::listView)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProblemResponse getProblemForStudent(Long id) {
        CodingProblem problem = problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> CodingException.problemNotFound(id));

        if (!problem.getEnabled()) {
            throw CodingException.problemNotFound(id);
        }

        return ProblemResponse.fromEntity(problem, true);
    }

    @Transactional(readOnly = true)
    public List<ProblemAdminResponse> getAllProblemsForAdmin() {
        List<CodingProblem> problems = problemRepository.findAllWithTestCases();
        return problems.stream()
                .map(ProblemAdminResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProblemAdminResponse getProblemForAdmin(Long id) {
        CodingProblem problem = problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> CodingException.problemNotFound(id));
        return ProblemAdminResponse.fromEntity(problem);
    }

    @Transactional
    public ProblemAdminResponse createProblem(CreateProblemRequest request) {
        validateEventNotStarted();

        if (problemRepository.existsByTitle(request.getTitle())) {
            throw CodingException.duplicateTitle(request.getTitle());
        }

        CodingProblem problem = CodingProblem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .constraints(request.getConstraints())
                .inputFormat(request.getInputFormat())
                .outputFormat(request.getOutputFormat())
                .points(request.getPoints())
                .timeLimitMs(request.getTimeLimitMs() != null ? request.getTimeLimitMs() : 2000)
                .memoryLimitMb(request.getMemoryLimitMb() != null ? request.getMemoryLimitMb() : 256)
                .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "MEDIUM")
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .enabled(true)
                .testCases(new ArrayList<>())
                .build();

        if (request.getTestCases() != null) {
            for (TestCaseRequest tcReq : request.getTestCases()) {
                TestCase testCase = createTestCase(tcReq);
                problem.addTestCase(testCase);
            }
        }

        problem = problemRepository.save(problem);
        log.info("Created problem: {} with {} test cases", problem.getTitle(), problem.getTestCases().size());

        return ProblemAdminResponse.fromEntity(problem);
    }

    @Transactional
    public ProblemAdminResponse updateProblem(Long id, UpdateProblemRequest request) {
        validateEventNotStarted();

        CodingProblem problem = problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> CodingException.problemNotFound(id));

        if (request.getTitle() != null && !request.getTitle().equals(problem.getTitle())) {
            if (problemRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
                throw CodingException.duplicateTitle(request.getTitle());
            }
            problem.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) problem.setDescription(request.getDescription());
        if (request.getConstraints() != null) problem.setConstraints(request.getConstraints());
        if (request.getInputFormat() != null) problem.setInputFormat(request.getInputFormat());
        if (request.getOutputFormat() != null) problem.setOutputFormat(request.getOutputFormat());
        if (request.getPoints() != null) problem.setPoints(request.getPoints());
        if (request.getTimeLimitMs() != null) problem.setTimeLimitMs(request.getTimeLimitMs());
        if (request.getMemoryLimitMb() != null) problem.setMemoryLimitMb(request.getMemoryLimitMb());
        if (request.getDifficulty() != null) problem.setDifficulty(request.getDifficulty());
        if (request.getDisplayOrder() != null) problem.setDisplayOrder(request.getDisplayOrder());
        if (request.getEnabled() != null) problem.setEnabled(request.getEnabled());

        if (request.getTestCases() != null) {
            problem.getTestCases().clear();
            for (TestCaseRequest tcReq : request.getTestCases()) {
                TestCase testCase = createTestCase(tcReq);
                problem.addTestCase(testCase);
            }
        }

        problem = problemRepository.save(problem);
        log.info("Updated problem: {}", problem.getTitle());

        return ProblemAdminResponse.fromEntity(problem);
    }

    @Transactional
    public void deleteProblem(Long id) {
        validateEventNotStarted();

        CodingProblem problem = problemRepository.findById(id)
                .orElseThrow(() -> CodingException.problemNotFound(id));

        problemRepository.delete(problem);
        log.info("Deleted problem: {}", problem.getTitle());
    }

    private TestCase createTestCase(TestCaseRequest request) {
        return TestCase.builder()
                .input(request.getInput())
                .expectedOutput(request.getExpectedOutput())
                .isSample(request.getIsSample() != null ? request.getIsSample() : false)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .explanation(request.getExplanation())
                .build();
    }

    private void validateEventNotStarted() {
        if (eventRepository.hasEventStartedOrCompleted()) {
            throw CodingException.cannotModifyAfterStart();
        }
    }

    @Transactional(readOnly = true)
    public CodingProblem getProblemEntity(Long id) {
        return problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> CodingException.problemNotFound(id));
    }
}
