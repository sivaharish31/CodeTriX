package com.codetrix.execution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseInput {

    private String input;
    private String expectedOutput;
    private Integer timeLimitMs;
    private Integer memoryLimitMb;
}
