package com.codetrix.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamLoginRequest {

    @NotBlank(message = "Team ID is required")
    @Size(min = 3, max = 20, message = "Team ID must be between 3 and 20 characters")
    private String teamId;

    @NotBlank(message = "Login PIN is required")
    @Size(min = 4, max = 100, message = "Login PIN must be between 4 and 100 characters")
    private String loginPin;
}
