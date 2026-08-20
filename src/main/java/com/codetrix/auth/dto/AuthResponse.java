package com.codetrix.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String accessToken;
    private String tokenType;
    private Long expiresIn;
    private String role;
    private String identifier;
    private String displayName;

    public static AuthResponse of(String accessToken, Long expiresIn, String role, String identifier, String displayName) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .role(role)
                .identifier(identifier)
                .displayName(displayName)
                .build();
    }
}
