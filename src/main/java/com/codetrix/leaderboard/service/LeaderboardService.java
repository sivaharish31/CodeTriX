package com.codetrix.leaderboard.service;

import com.codetrix.leaderboard.dto.*;
import com.codetrix.leaderboard.entity.ScoreRecord;
import com.codetrix.leaderboard.entity.TeamScore;
import com.codetrix.leaderboard.repository.ScoreRecordRepository;
import com.codetrix.leaderboard.repository.TeamScoreRepository;
import com.codetrix.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final TeamScoreRepository teamScoreRepository;
    private final ScoreRecordRepository scoreRecordRepository;
    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public LeaderboardResponse getLeaderboard() {
        List<TeamScore> teamScores = teamScoreRepository.findAllOrderedForLeaderboard();

        List<LeaderboardEntryResponse> entries = new ArrayList<>();
        int rank = 1;
        int previousRank = 1;
        Integer previousScore = null;
        LocalDateTime previousTime = null;

        for (int i = 0; i < teamScores.size(); i++) {
            TeamScore ts = teamScores.get(i);

            if (previousScore != null && !ts.getTotalScore().equals(previousScore)) {
                rank = i + 1;
            } else if (previousScore != null && ts.getTotalScore().equals(previousScore)) {
                if (previousTime != null && ts.getLastSubmissionTime() != null
                    && !ts.getLastSubmissionTime().equals(previousTime)) {
                    rank = i + 1;
                }
            }

            entries.add(LeaderboardEntryResponse.from(ts, rank));

            previousScore = ts.getTotalScore();
            previousTime = ts.getLastSubmissionTime();
            previousRank = rank;
        }

        long totalTeams = teamRepository.count();

        return LeaderboardResponse.builder()
            .entries(entries)
            .totalTeams((int) totalTeams)
            .generatedAt(LocalDateTime.now())
            .build();
    }

    @Transactional(readOnly = true)
    public TeamScoreResponse getTeamScore(Long teamId) {
        Optional<TeamScore> teamScoreOpt = teamScoreRepository.findByTeamId(teamId);

        if (teamScoreOpt.isEmpty()) {
            throw new RuntimeException("Team score not found for team: " + teamId);
        }

        TeamScore teamScore = teamScoreOpt.get();
        List<TeamScore> allScores = teamScoreRepository.findAllOrderedForLeaderboard();

        int rank = 1;
        for (int i = 0; i < allScores.size(); i++) {
            if (allScores.get(i).getTeam().getId().equals(teamId)) {
                rank = calculateRankWithTieBreaking(allScores, i);
                break;
            }
        }

        return TeamScoreResponse.from(teamScore, rank, allScores.size());
    }

    @Transactional(readOnly = true)
    public AdminScoresResponse getAdminScores() {
        List<TeamScore> teamScores = teamScoreRepository.findAllOrderedForLeaderboard();

        List<LeaderboardEntryResponse> entries = new ArrayList<>();
        int rank = 1;
        Integer previousScore = null;

        for (int i = 0; i < teamScores.size(); i++) {
            TeamScore ts = teamScores.get(i);
            if (previousScore != null && !ts.getTotalScore().equals(previousScore)) {
                rank = i + 1;
            }
            entries.add(LeaderboardEntryResponse.from(ts, rank));
            previousScore = ts.getTotalScore();
        }

        List<ScoreRecord> recentRecords = scoreRecordRepository.findAll()
            .stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .limit(50)
            .collect(Collectors.toList());

        List<ScoreRecordResponse> recordResponses = recentRecords.stream()
            .map(ScoreRecordResponse::from)
            .collect(Collectors.toList());

        int totalCoding = teamScores.stream().mapToInt(TeamScore::getCodingScore).sum();
        int totalDebugging = teamScores.stream().mapToInt(TeamScore::getDebuggingScore).sum();
        int totalCtf = teamScores.stream().mapToInt(TeamScore::getCtfScore).sum();
        int teamsWithScore = (int) teamScores.stream().filter(ts -> ts.getTotalScore() > 0).count();

        AdminScoresResponse.ScoreSummary summary = AdminScoresResponse.ScoreSummary.builder()
            .totalTeams(teamScores.size())
            .teamsWithScore(teamsWithScore)
            .totalCodingPoints(totalCoding)
            .totalDebuggingPoints(totalDebugging)
            .totalCtfPoints(totalCtf)
            .totalPoints(totalCoding + totalDebugging + totalCtf)
            .build();

        return AdminScoresResponse.builder()
            .teamScores(entries)
            .recentRecords(recordResponses)
            .summary(summary)
            .build();
    }

    private int calculateRankWithTieBreaking(List<TeamScore> scores, int index) {
        if (index == 0) return 1;

        TeamScore current = scores.get(index);
        int rank = 1;

        for (int i = 0; i < index; i++) {
            TeamScore other = scores.get(i);
            if (other.getTotalScore() > current.getTotalScore()) {
                rank++;
            } else if (other.getTotalScore().equals(current.getTotalScore())) {
                if (other.getLastSubmissionTime() != null && current.getLastSubmissionTime() != null) {
                    if (other.getLastSubmissionTime().isBefore(current.getLastSubmissionTime())) {
                        rank++;
                    } else if (other.getLastSubmissionTime().equals(current.getLastSubmissionTime())) {
                        if (other.getTeam().getTeamName().compareTo(current.getTeam().getTeamName()) < 0) {
                            rank++;
                        }
                    }
                }
            }
        }

        return rank;
    }
}
