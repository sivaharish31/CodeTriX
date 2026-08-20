package com.codetrix.leaderboard.repository;

import com.codetrix.leaderboard.entity.TeamScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamScoreRepository extends JpaRepository<TeamScore, Long> {

    Optional<TeamScore> findByTeamId(Long teamId);

    @Query("""
        SELECT ts FROM TeamScore ts
        JOIN FETCH ts.team t
        ORDER BY ts.totalScore DESC, ts.lastSubmissionTime ASC, t.teamName ASC
        """)
    List<TeamScore> findAllOrderedForLeaderboard();

    @Query("""
        SELECT COUNT(ts) + 1 FROM TeamScore ts
        WHERE ts.totalScore > :score
        OR (ts.totalScore = :score AND ts.lastSubmissionTime < :submissionTime)
        OR (ts.totalScore = :score AND ts.lastSubmissionTime = :submissionTime AND ts.team.teamName < :teamName)
        """)
    int calculateRank(
        @Param("score") Integer score,
        @Param("submissionTime") java.time.LocalDateTime submissionTime,
        @Param("teamName") String teamName
    );

    boolean existsByTeamId(Long teamId);
}
