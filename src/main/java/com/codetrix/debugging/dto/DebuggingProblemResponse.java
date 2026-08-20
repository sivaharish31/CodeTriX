package com.codetrix.debugging.dto;

import com.codetrix.coding.entity.Language;
import com.codetrix.debugging.entity.DebuggingProblem;
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
public class DebuggingProblemResponse {

    private Long id;
    private String title;
    private String description;
    private String buggyCode;
    private Language language;
    private Integer points;
    private Integer timeLimitMs;
    private Integer memoryLimitMb;
    private String hint;
    private Integer displayOrder;
    private List<DebuggingTestCaseResponse> sampleTestCases;
    private Integer totalTestCases;

    public static DebuggingProblemResponse fromEntity(DebuggingProblem problem, boolean includeSamples) {
        DebuggingProblemResponseBuilder builder = DebuggingProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .description(problem.getDescription())
                .buggyCode(problem.getBuggyCode())
                .language(problem.getLanguage())
                .points(problem.getPoints())
                .timeLimitMs(problem.getTimeLimitMs())
                .memoryLimitMb(problem.getMemoryLimitMb())
                .hint(problem.getHint())
                .displayOrder(problem.getDisplayOrder())
                .totalTestCases(problem.getTestCases() != null ? problem.getTestCases().size() : 0);

        if (includeSamples && problem.getTestCases() != null) {
            List<DebuggingTestCaseResponse> samples = problem.getSampleTestCases().stream()
                    .map(DebuggingTestCaseResponse::fromEntity)
                    .toList();
            builder.sampleTestCases(samples);
        }

        return builder.build();
    }

    public static DebuggingProblemResponse listView(DebuggingProblem problem) {
        return DebuggingProblemResponse.builder()
                .id(problem.getId())
                .title(problem.getTitle())
                .language(problem.getLanguage())
                .points(problem.getPoints())
                .displayOrder(problem.getDisplayOrder())
                .build();
    }
}
