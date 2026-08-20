package com.codetrix.debugging.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDebuggingProblemRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Buggy code is required")
    private String buggyCode;

    @NotBlank(message = "Language is required")
    private String language;

    @NotNull(message = "Points is required")
    @Min(value = 10, message = "Points must be at least 10")
    @Max(value = 1000, message = "Points cannot exceed 1000")
    private Integer points;

    @Min(value = 500, message = "Time limit must be at least 500ms")
    @Max(value = 10000, message = "Time limit cannot exceed 10000ms")
    private Integer timeLimitMs = 2000;

    @Min(value = 16, message = "Memory limit must be at least 16MB")
    @Max(value = 512, message = "Memory limit cannot exceed 512MB")
    private Integer memoryLimitMb = 256;

    private String hint;

    private Integer displayOrder = 0;

    @Valid
    private List<DebuggingTestCaseRequest> testCases;
}
