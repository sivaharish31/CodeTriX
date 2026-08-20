package com.codetrix.team.dto;

import com.codetrix.team.entity.Team;
import com.codetrix.team.entity.TeamStatus;
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
public class TeamResponse {

    private Long id;
    private String teamCode;
    private String teamName;
    private TeamStatus status;
    private int memberCount;
    private List<MemberResponse> members;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TeamResponse fromEntity(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .teamCode(team.getTeamCode())
                .teamName(team.getTeamName())
                .status(team.getStatus())
                .memberCount(team.getMemberCount())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }

    public static TeamResponse fromEntityWithMembers(Team team) {
        List<MemberResponse> memberResponses = team.getMembers() != null
                ? team.getMembers().stream()
                    .map(MemberResponse::fromEntity)
                    .toList()
                : List.of();

        return TeamResponse.builder()
                .id(team.getId())
                .teamCode(team.getTeamCode())
                .teamName(team.getTeamName())
                .status(team.getStatus())
                .memberCount(memberResponses.size())
                .members(memberResponses)
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }
}
