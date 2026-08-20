package com.codetrix.leaderboard.dto;

import com.codetrix.leaderboard.entity.TeamScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryResponse {

    private Integer rank;
    private Long teamId;
    private String teamName;
    private Integer codingScore;
    private Integer debuggingScore;
    private Integer ctfScore;
    private Integer totalScore;
    private Integer codingProblemsSolved;
    private Integer debuggingProblemsSolved;
    private Integer ctfChallengesSolved;
    private LocalDateTime lastSubmissionTime;

    public static LeaderboardEntryResponse from(TeamScore teamScore, int rank) {
        return LeaderboardEntryResponse.builder()
            .rank(rank)
            .teamId(teamScore.getTeam().getId())
            .teamName(teamScore.getTeam().getTeamName())
            .codingScore(teamScore.getCodingScore())
            .debuggingScore(teamScore.getDebuggingScore())
            .ctfScore(teamScore.getCtfScore())
            .totalScore(teamScore.getTotalScore())
            .codingProblemsSolved(teamScore.getCodingProblemsSolved())
            .debuggingProblemsSolved(teamScore.getDebuggingProblemsSolved())
            .ctfChallengesSolved(teamScore.getCtfChallengesSolved())
            .lastSubmissionTime(teamScore.getLastSubmissionTime())
            .build();
    }
}
