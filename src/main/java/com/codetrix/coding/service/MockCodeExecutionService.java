package com.codetrix.coding.service;

import com.codetrix.coding.entity.Language;
import com.codetrix.coding.entity.Submission;
import com.codetrix.coding.entity.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(name = "execution.mock-enabled", havingValue = "true")
public class MockCodeExecutionService implements CodeExecutionService {

    @Override
    public ExecutionResult runCode(String sourceCode, Language language, String input, int timeLimitMs, int memoryLimitMb) {
        log.info("Mock executing code in {} with custom input", language);
        return ExecutionResult.success(
            "Mock output for custom input\n",
            150,
            1024
        );
    }

    @Override
    public JudgeResult judgeSubmission(Submission submission, List<TestCase> testCases, int timeLimitMs, int memoryLimitMb) {
        log.info("Mock judging submission {} for problem {} in {}",
            submission.getId(), submission.getProblemId(), submission.getLanguage());

        int totalTests = testCases.size();
        int passedTests = totalTests;

        return new JudgeResult(
            "ACCEPTED",
            passedTests,
            totalTests,
            100,
            200,
            2048,
            null,
            null
        );
    }
}
