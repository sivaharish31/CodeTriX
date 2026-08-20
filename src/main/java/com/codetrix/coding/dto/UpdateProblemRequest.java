package com.codetrix.coding.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProblemRequest {

    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    private String description;

    private String constraints;

    private String inputFormat;

    private String outputFormat;

    @Min(value = 10, message = "Points must be at least 10")
    @Max(value = 1000, message = "Points cannot exceed 1000")
    private Integer points;

    @Min(value = 500, message = "Time limit must be at least 500ms")
    @Max(value = 10000, message = "Time limit cannot exceed 10000ms")
    private Integer timeLimitMs;

    @Min(value = 16, message = "Memory limit must be at least 16MB")
    @Max(value = 512, message = "Memory limit cannot exceed 512MB")
    private Integer memoryLimitMb;

    private String difficulty;

    private Integer displayOrder;

    private Boolean enabled;

    @Valid
    private List<TestCaseRequest> testCases;
}
