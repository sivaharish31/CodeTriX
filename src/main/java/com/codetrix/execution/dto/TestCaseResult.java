package com.codetrix.execution.dto;

import com.codetrix.execution.entity.ExecutionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResult {

    private Integer testCaseIndex;
    private ExecutionStatus status;
    private Boolean passed;
    private Long executionTimeMs;
    private Long memoryUsedKb;
    private String actualOutput;
    private String errorOutput;
}
