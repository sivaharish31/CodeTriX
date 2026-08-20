package com.codetrix.ctf.repository;

import com.codetrix.ctf.entity.CtfSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CtfSubmissionRepository extends JpaRepository<CtfSubmission, Long> {

    List<CtfSubmission> findByTeamIdOrderBySubmissionTimeDesc(Long teamId);

    List<CtfSubmission> findByTeamIdAndCorrectTrueOrderBySubmissionTimeDesc(Long teamId);

    boolean existsByTeamIdAndChallengeIdAndCorrectTrue(Long teamId, Long challengeId);

    Optional<CtfSubmission> findFirstByTeamIdAndChallengeIdAndCorrectTrue(Long teamId, Long challengeId);

    @Query("SELECT COUNT(s) FROM CtfSubmission s WHERE s.team.id = :teamId AND s.challenge.id = :challengeId AND s.submissionTime > :since")
    long countRecentAttempts(
        @Param("teamId") Long teamId,
        @Param("challengeId") Long challengeId,
        @Param("since") LocalDateTime since
    );

    @Query("SELECT COALESCE(SUM(s.pointsAwarded), 0) FROM CtfSubmission s WHERE s.team.id = :teamId AND s.correct = true")
    Integer getTotalPointsByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT COUNT(DISTINCT s.challenge.id) FROM CtfSubmission s WHERE s.team.id = :teamId AND s.correct = true")
    Integer countSolvedChallengesByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT s.challenge.id FROM CtfSubmission s WHERE s.team.id = :teamId AND s.correct = true")
    List<Long> findSolvedChallengeIdsByTeamId(@Param("teamId") Long teamId);
}
