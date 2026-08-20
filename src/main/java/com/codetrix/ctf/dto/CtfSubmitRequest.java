package com.codetrix.ctf.dto;

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
public class CtfSubmitRequest {

    @NotNull(message = "Challenge ID is required")
    private Long challengeId;

    @NotBlank(message = "Flag is required")
    private String flag;
}
