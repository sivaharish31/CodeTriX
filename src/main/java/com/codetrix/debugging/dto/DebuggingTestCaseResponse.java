package com.codetrix.debugging.dto;

import com.codetrix.debugging.entity.DebuggingTestCase;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DebuggingTestCaseResponse {

    private Long id;
    private String input;
    private String expectedOutput;
    private Boolean isSample;
    private Integer displayOrder;
    private String explanation;

    public static DebuggingTestCaseResponse fromEntity(DebuggingTestCase testCase) {
        return DebuggingTestCaseResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .isSample(testCase.getIsSample())
                .displayOrder(testCase.getDisplayOrder())
                .explanation(testCase.getExplanation())
                .build();
    }
}
