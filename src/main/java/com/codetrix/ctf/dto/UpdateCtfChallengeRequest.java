package com.codetrix.ctf.dto;

import com.codetrix.ctf.entity.CtfCategory;
import com.codetrix.ctf.entity.CtfDifficulty;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCtfChallengeRequest {

    private String title;

    private String description;

    private CtfCategory category;

    private CtfDifficulty difficulty;

    @Min(value = 1, message = "Points must be at least 1")
    private Integer points;

    private String flag;

    private Boolean active;
}
