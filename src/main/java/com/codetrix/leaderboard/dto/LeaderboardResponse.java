package com.codetrix.leaderboard.dto;

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
public class LeaderboardResponse {

    private List<LeaderboardEntryResponse> entries;
    private Integer totalTeams;
    private LocalDateTime generatedAt;
    private String eventStatus;
}
