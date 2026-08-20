package com.codetrix.debugging.dto;

import com.codetrix.coding.entity.Language;
import com.codetrix.debugging.entity.DebuggingProblem;
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
public class DebuggingProblemAdminResponse {

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
    private Boolean enabled;
    private List<DebuggingTestCaseResponse> testCases;
    private Integer sampleTestCaseCount;
    private Integer hiddenTestCaseCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static DebuggingProblemAdminResponse fromEntity(DebuggingProblem problem) {
        List<DebuggingTestCaseResponse> allTestCases = problem.getTestCases() != null
                ? problem.getTestCases().stream()
                    .map(DebuggingTestCaseResponse::fromEntity)
                    .toList()
                : List.of();

        int sampleCount = (int) allTestCases.stream().filter(tc -> Boolean.TRUE.equals(tc.getIsSample())).count();
        int hiddenCount = allTestCases.size() - sampleCount;

        return DebuggingProblemAdminResponse.builder()
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
                .enabled(problem.getEnabled())
                .testCases(allTestCases)
                .sampleTestCaseCount(sampleCount)
                .hiddenTestCaseCount(hiddenCount)
                .createdAt(problem.getCreatedAt())
                .updatedAt(problem.getUpdatedAt())
                .build();
    }
}
