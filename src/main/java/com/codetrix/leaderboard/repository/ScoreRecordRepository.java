package com.codetrix.leaderboard.repository;

import com.codetrix.leaderboard.entity.ScoreRecord;
import com.codetrix.leaderboard.entity.ScoreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRecordRepository extends JpaRepository<ScoreRecord, Long> {

    Optional<ScoreRecord> findByTeamIdAndScoreTypeAndProblemId(Long teamId, ScoreType scoreType, Long problemId);

    boolean existsByTeamIdAndScoreTypeAndProblemId(Long teamId, ScoreType scoreType, Long problemId);

    List<ScoreRecord> findByTeamIdOrderByCreatedAtDesc(Long teamId);

    List<ScoreRecord> findByTeamIdAndScoreTypeOrderByCreatedAtDesc(Long teamId, ScoreType scoreType);

    @Query("SELECT COALESCE(SUM(sr.pointsEarned), 0) FROM ScoreRecord sr WHERE sr.team.id = :teamId AND sr.scoreType = :scoreType")
    Integer sumPointsByTeamAndType(@Param("teamId") Long teamId, @Param("scoreType") ScoreType scoreType);

    @Query("SELECT COUNT(sr) FROM ScoreRecord sr WHERE sr.team.id = :teamId AND sr.scoreType = :scoreType")
    Integer countByTeamAndType(@Param("teamId") Long teamId, @Param("scoreType") ScoreType scoreType);
}
