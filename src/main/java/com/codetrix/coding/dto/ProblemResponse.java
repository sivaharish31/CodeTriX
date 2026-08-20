package com.codetrix.coding.dto;

import com.codetrix.coding.entity.CodingProblem;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemResponse {

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
    private List<TestCaseResponse> sampleTestCases;
    private Integer totalTestCases;

    public static ProblemResponse fromEntity(CodingProblem problem, boolean includeSamples) {
        ProblemResponseBuilder builder = ProblemResponse.builder()
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
                .totalTestCases(problem.getTestCases() != null ? problem.getTestCases().size() : 0);

        if (includeSamples && problem.getTestCases() != null) {
            List<TestCaseResponse> samples = problem.getSampleTestCases().stream()
                    .map(TestCaseResponse::fromEntity)
                    .toList();
            builder.sampleTestCases(samples);
        }

        return builder.build();
    }

    public static ProblemResponse listView(CodingProblem problem) {
        return ProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .points(problem.getPoints())
                .difficulty(problem.getDifficulty())
                .displayOrder(problem.getDisplayOrder())
                .build();
    }
}
