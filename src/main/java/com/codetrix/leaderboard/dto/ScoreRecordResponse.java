package com.codetrix.leaderboard.dto;

import com.codetrix.leaderboard.entity.ScoreRecord;
import com.codetrix.leaderboard.entity.ScoreType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRecordResponse {

    private Long id;
    private Long teamId;
    private String teamName;
    private ScoreType scoreType;
    private Long problemId;
    private Long submissionId;
    private Integer pointsEarned;
    private Integer maxPoints;
    private Integer testsPassed;
    private Integer totalTests;
    private LocalDateTime createdAt;

    public static ScoreRecordResponse from(ScoreRecord record) {
        return ScoreRecordResponse.builder()
            .id(record.getId())
            .teamId(record.getTeam().getId())
            .teamName(record.getTeam().getTeamName())
            .scoreType(record.getScoreType())
            .problemId(record.getProblemId())
            .submissionId(record.getSubmissionId())
            .pointsEarned(record.getPointsEarned())
            .maxPoints(record.getMaxPoints())
            .testsPassed(record.getTestsPassed())
            .totalTests(record.getTotalTests())
            .createdAt(record.getCreatedAt())
            .build();
    }
}
