package com.codetrix.execution.dto;

import com.codetrix.execution.entity.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionResult {

    private ExecutionStatus status;
    private Integer passedTests;
    private Integer totalTests;
    private Long executionTimeMs;
    private Long memoryUsedKb;
    private String compilerOutput;
    private String runtimeOutput;
    private String errorOutput;
    private List<TestCaseResult> testCaseResults;

    public static ExecutionResult compilationError(String compilerOutput) {
        return ExecutionResult.builder()
            .status(ExecutionStatus.COMPILATION_ERROR)
            .passedTests(0)
            .totalTests(0)
            .compilerOutput(compilerOutput)
            .build();
    }

    public static ExecutionResult internalError(String message) {
        return ExecutionResult.builder()
            .status(ExecutionStatus.INTERNAL_ERROR)
            .passedTests(0)
            .totalTests(0)
            .errorOutput(message)
            .build();
    }

    public static ExecutionResult customRunResult(ExecutionStatus status, String output, String error, Long timeMs) {
        return ExecutionResult.builder()
            .status(status)
            .runtimeOutput(output)
            .errorOutput(error)
            .executionTimeMs(timeMs)
            .build();
    }
}
