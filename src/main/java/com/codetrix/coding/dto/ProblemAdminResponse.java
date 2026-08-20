package com.codetrix.coding.dto;

import com.codetrix.coding.entity.CodingProblem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemAdminResponse {

    private Long id;
    private String title;
    private String description;
    private String constraints;
    private String inputFormat;
    private String outputFormat;
    private Integer points;
    private Integer timeLimitMs;
    private Integer memoryLimitMb;
    private String difficulty;
    private Integer displayOrder;
    private Boolean enabled;
    private List<TestCaseResponse> testCases;
    private Integer sampleTestCaseCount;
    private Integer hiddenTestCaseCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProblemAdminResponse fromEntity(CodingProblem problem) {
        List<TestCaseResponse> allTestCases = problem.getTestCases() != null
                ? problem.getTestCases().stream()
                    .map(TestCaseResponse::fromEntityWithOutput)
                    .toList()
                : List.of();

        int sampleCount = (int) allTestCases.stream().filter(tc -> Boolean.TRUE.equals(tc.getIsSample())).count();
        int hiddenCount = allTestCases.size() - sampleCount;

        return ProblemAdminResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .constraints(problem.getConstraints())
                .inputFormat(problem.getInputFormat())
                .outputFormat(problem.getOutputFormat())
                .points(problem.getPoints())
                .timeLimitMs(problem.getTimeLimitMs())
                .memoryLimitMb(problem.getMemoryLimitMb())
                .difficulty(problem.getDifficulty())
                .displayOrder(problem.getDisplayOrder())
                .enabled(problem.getEnabled())
                .testCases(allTestCases)
                .sampleTestCaseCount(sampleCount)
                .hiddenTestCaseCount(hiddenCount)
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .build();
    }
}
