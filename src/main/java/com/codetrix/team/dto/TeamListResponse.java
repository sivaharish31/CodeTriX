package com.codetrix.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamListResponse {

    private List<TeamResponse> teams;
    private int totalTeams;
    private int maxTeams;
    private int availableSlots;

    public static TeamListResponse of(List<TeamResponse> teams, int maxTeams) {
        return TeamListResponse.builder()
                .teams(teams)
                .totalTeams(teams.size())
                .maxTeams(maxTeams)
                .availableSlots(Math.max(0, maxTeams - teams.size()))
                .build();
    }
}
