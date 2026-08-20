package com.codetrix.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCredentialsResponse {

    private Long teamId;
    private String teamCode;
    private String teamName;
    private String loginPin;
    private String message;

    public static TeamCredentialsResponse of(Long teamId, String teamCode, String teamName, String loginPin) {
        return TeamCredentialsResponse.builder()
                .teamId(teamId)
                .teamCode(teamCode)
                .teamName(teamName)
                .loginPin(loginPin)
                .message("Please share these credentials securely with the team. The PIN cannot be retrieved again.")
                .build();
    }
}
