package com.codetrix.leaderboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminScoresResponse {

    private List<LeaderboardEntryResponse> teamScores;
    private List<ScoreRecordResponse> recentRecords;
    private ScoreSummary summary;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreSummary {
        private Integer totalTeams;
        private Integer teamsWithScore;
        private Integer totalCodingPoints;
        private Integer totalDebuggingPoints;
        private Integer totalCtfPoints;
        private Integer totalPoints;
    }
}
