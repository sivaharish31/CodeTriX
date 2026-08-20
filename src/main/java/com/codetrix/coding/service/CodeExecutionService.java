package com.codetrix.coding.service;

import com.codetrix.coding.entity.Language;
import com.codetrix.coding.entity.Submission;
import com.codetrix.coding.entity.TestCase;

import java.util.List;

public interface CodeExecutionService {

    ExecutionResult runCode(String sourceCode, Language language, String input, int timeLimitMs, int memoryLimitMb);

    JudgeResult judgeSubmission(Submission submission, List<TestCase> testCases, int timeLimitMs, int memoryLimitMb);

    record ExecutionResult(
        boolean success,
        String status,
        String output,
        String error,
        String compileOutput,
        Integer executionTimeMs,
        Integer memoryUsedKb
    ) {
        public static ExecutionResult success(String output, int timeMs, int memoryKb) {
            return new ExecutionResult(true, "SUCCESS", output, null, null, timeMs, memoryKb);
        }

        public static ExecutionResult compileError(String error) {
            return new ExecutionResult(false, "COMPILE_ERROR", null, null, error, null, null);
        }

        public static ExecutionResult runtimeError(String error) {
            return new ExecutionResult(false, "RUNTIME_ERROR", null, error, null, null, null);
        }

        public static ExecutionResult timeLimit() {
            return new ExecutionResult(false, "TIME_LIMIT", null, "Time limit exceeded", null, null, null);
        }
    }

    record JudgeResult(
        String status,
        int testsPassed,
        int totalTests,
        int pointsEarned,
        Integer executionTimeMs,
        Integer memoryUsedKb,
        String compileOutput,
        String errorMessage
    ) {}
}
