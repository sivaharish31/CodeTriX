package com.codetrix.execution.dto;

import com.codetrix.execution.entity.ExecutionLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionRequest {

    @NotNull(message = "Language is required")
    private ExecutionLanguage language;

    @NotBlank(message = "Source code is required")
    private String sourceCode;

    private List<TestCaseInput> testCases;

    @Builder.Default
    private Integer timeLimitMs = 2000;

    @Builder.Default
    private Integer memoryLimitMb = 256;

    @Builder.Default
    private Boolean customRun = false;

    private String customInput;

    private String submissionId;
}
