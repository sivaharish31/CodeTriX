package com.codetrix.ctf.dto;

import com.codetrix.ctf.entity.CtfCategory;
import com.codetrix.ctf.entity.CtfDifficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCtfChallengeRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Category is required")
    private CtfCategory category;

    @NotNull(message = "Difficulty is required")
    private CtfDifficulty difficulty;

    @NotNull(message = "Points is required")
    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;

    @NotBlank(message = "Flag is required")
    private String flag;

    @Builder.Default
    private Boolean active = true;
}
