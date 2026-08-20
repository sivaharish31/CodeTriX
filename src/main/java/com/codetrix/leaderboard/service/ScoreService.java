package com.codetrix.leaderboard.service;

import com.codetrix.leaderboard.entity.ScoreRecord;
import com.codetrix.leaderboard.entity.ScoreType;
import com.codetrix.leaderboard.entity.TeamScore;
import com.codetrix.leaderboard.repository.ScoreRecordRepository;
import com.codetrix.leaderboard.repository.TeamScoreRepository;
import com.codetrix.team.entity.Team;
import com.codetrix.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreService {

    private final TeamScoreRepository teamScoreRepository;
    private final ScoreRecordRepository scoreRecordRepository;
    private final TeamRepository teamRepository;
    private final LeaderboardBroadcaster leaderboardBroadcaster;

    @Transactional
    public void recordCodingScore(Long teamId, Long problemId, Long submissionId,
                                   int testsPassed, int totalTests, int maxPoints) {
        int pointsEarned = calculatePartialScore(testsPassed, totalTests, maxPoints);

        Optional<ScoreRecord> existingRecord = scoreRecordRepository
            .findByTeamIdAndScoreTypeAndProblemId(teamId, ScoreType.CODING, problemId);

        if (existingRecord.isPresent()) {
            ScoreRecord record = existingRecord.get();
            if (pointsEarned > record.getPointsEarned()) {
                int scoreDifference = pointsEarned - record.getPointsEarned();
                record.setPointsEarned(pointsEarned);
                record.setSubmissionId(submissionId);
                record.setTestsPassed(testsPassed);
                record.setTotalTests(totalTests);
                scoreRecordRepository.save(record);

                updateTeamScore(teamId, ScoreType.CODING, scoreDifference, false);
                log.info("Updated coding score for team {}, problem {}: {} -> {} points",
                    teamId, problemId, record.getPointsEarned() - scoreDifference, pointsEarned);
            }
        } else {
            createScoreRecord(teamId, ScoreType.CODING, problemId, submissionId,
                pointsEarned, maxPoints, testsPassed, totalTests);
            updateTeamScore(teamId, ScoreType.CODING, pointsEarned, true);
            log.info("New coding score for team {}, problem {}: {} points", teamId, problemId, pointsEarned);
        }

        leaderboardBroadcaster.broadcastUpdate();
    }

    @Transactional
    public void recordDebuggingScore(Long teamId, Long problemId, Long submissionId,
                                      int testsPassed, int totalTests, int maxPoints) {
        int pointsEarned = calculatePartialScore(testsPassed, totalTests, maxPoints);

        Optional<ScoreRecord> existingRecord = scoreRecordRepository
            .findByTeamIdAndScoreTypeAndProblemId(teamId, ScoreType.DEBUGGING, problemId);

        if (existingRecord.isPresent()) {
            ScoreRecord record = existingRecord.get();
            if (pointsEarned > record.getPointsEarned()) {
                int scoreDifference = pointsEarned - record.getPointsEarned();
                record.setPointsEarned(pointsEarned);
                record.setSubmissionId(submissionId);
                record.setTestsPassed(testsPassed);
                record.setTotalTests(totalTests);
                scoreRecordRepository.save(record);

                updateTeamScore(teamId, ScoreType.DEBUGGING, scoreDifference, false);
                log.info("Updated debugging score for team {}, problem {}: {} points", teamId, problemId, pointsEarned);
            }
        } else {
            createScoreRecord(teamId, ScoreType.DEBUGGING, problemId, submissionId,
                pointsEarned, maxPoints, testsPassed, totalTests);
            updateTeamScore(teamId, ScoreType.DEBUGGING, pointsEarned, true);
            log.info("New debugging score for team {}, problem {}: {} points", teamId, problemId, pointsEarned);
        }

        leaderboardBroadcaster.broadcastUpdate();
    }

    @Transactional
    public boolean recordCtfScore(Long teamId, Long challengeId, Long submissionId, int points) {
        if (scoreRecordRepository.existsByTeamIdAndScoreTypeAndProblemId(teamId, ScoreType.CTF, challengeId)) {
            log.debug("CTF score already recorded for team {}, challenge {}", teamId, challengeId);
            return false;
        }

        createScoreRecord(teamId, ScoreType.CTF, challengeId, submissionId, points, points, null, null);
        updateTeamScore(teamId, ScoreType.CTF, points, true);
        log.info("CTF score for team {}, challenge {}: {} points", teamId, challengeId, points);

        leaderboardBroadcaster.broadcastUpdate();
        return true;
    }

    private int calculatePartialScore(int testsPassed, int totalTests, int maxPoints) {
        if (totalTests == 0) return 0;
        return (int) Math.floor((double) testsPassed / totalTests * maxPoints);
    }

    private void createScoreRecord(Long teamId, ScoreType type, Long problemId, Long submissionId,
                                    int pointsEarned, int maxPoints, Integer testsPassed, Integer totalTests) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        ScoreRecord record = ScoreRecord.builder()
            .team(team)
            .scoreType(type)
            .problemId(problemId)
            .submissionId(submissionId)
            .pointsEarned(pointsEarned)
            .maxPoints(maxPoints)
            .testsPassed(testsPassed)
            .totalTests(totalTests)
            .build();

        scoreRecordRepository.save(record);
    }

    private void updateTeamScore(Long teamId, ScoreType type, int pointsDelta, boolean newProblem) {
        TeamScore teamScore = teamScoreRepository.findByTeamId(teamId)
            .orElseGet(() -> createTeamScore(teamId));

        switch (type) {
            case CODING -> {
                teamScore.setCodingScore(teamScore.getCodingScore() + pointsDelta);
                if (newProblem) {
                    teamScore.setCodingProblemsSolved(teamScore.getCodingProblemsSolved() + 1);
                }
            }
            case DEBUGGING -> {
                teamScore.setDebuggingScore(teamScore.getDebuggingScore() + pointsDelta);
                if (newProblem) {
                    teamScore.setDebuggingProblemsSolved(teamScore.getDebuggingProblemsSolved() + 1);
                }
            }
            case CTF -> {
                teamScore.setCtfScore(teamScore.getCtfScore() + pointsDelta);
                if (newProblem) {
                    teamScore.setCtfChallengesSolved(teamScore.getCtfChallengesSolved() + 1);
                }
            }
        }

        teamScore.setLastSubmissionTime(LocalDateTime.now());
        teamScore.recalculateTotal();
        teamScoreRepository.save(teamScore);
    }

    private TeamScore createTeamScore(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        TeamScore teamScore = TeamScore.builder()
            .team(team)
            .codingScore(0)
            .debuggingScore(0)
            .ctfScore(0)
            .totalScore(0)
            .codingProblemsSolved(0)
            .debuggingProblemsSolved(0)
            .ctfChallengesSolved(0)
            .build();

        return teamScoreRepository.save(teamScore);
    }

    @Transactional
    public void initializeTeamScore(Long teamId) {
        if (!teamScoreRepository.existsByTeamId(teamId)) {
            createTeamScore(teamId);
            log.info("Initialized score for team {}", teamId);
        }
    }
}
