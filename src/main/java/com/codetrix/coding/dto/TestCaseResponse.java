package com.codetrix.coding.dto;

import com.codetrix.coding.entity.TestCase;
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
public class TestCaseResponse {

    private Long id;
    private String input;
    private String expectedOutput;
    private Boolean isSample;
    private Integer displayOrder;
    private String explanation;

    public static TestCaseResponse fromEntity(TestCase testCase) {
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .isSample(testCase.getIsSample())
                .displayOrder(testCase.getDisplayOrder())
                .explanation(testCase.getExplanation())
                .build();
    }

    public static TestCaseResponse fromEntityWithOutput(TestCase testCase) {
        return TestCaseResponse.builder()
                .id(testCase.getId())
                .input(testCase.getInput())
                .expectedOutput(testCase.getExpectedOutput())
                .isSample(testCase.getIsSample())
                .displayOrder(testCase.getDisplayOrder())
                .explanation(testCase.getExplanation())
                .build();
    }
}
