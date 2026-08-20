package com.codetrix.debugging.repository;

import com.codetrix.coding.entity.SubmissionStatus;
import com.codetrix.debugging.entity.DebuggingSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebuggingSubmissionRepository extends JpaRepository<DebuggingSubmission, Long> {

    List<DebuggingSubmission> findByTeamIdOrderBySubmissionTimeDesc(Long teamId);

    List<DebuggingSubmission> findByTeamIdAndProblemIdOrderBySubmissionTimeDesc(Long teamId, Long problemId);

    List<DebuggingSubmission> findByStatus(SubmissionStatus status);

    @Query("SELECT s FROM DebuggingSubmission s WHERE s.teamId = :teamId AND s.problemId = :problemId AND s.status = 'ACCEPTED' ORDER BY s.submissionTime DESC LIMIT 1")
    Optional<DebuggingSubmission> findAcceptedSubmission(Long teamId, Long problemId);

    @Query("SELECT s FROM DebuggingSubmission s WHERE s.teamId = :teamId AND s.problemId = :problemId ORDER BY s.pointsEarned DESC LIMIT 1")
    Optional<DebuggingSubmission> findBestSubmission(Long teamId, Long problemId);

    @Query("SELECT DISTINCT s.problemId FROM DebuggingSubmission s WHERE s.teamId = :teamId AND s.status = 'ACCEPTED'")
    List<Long> findSolvedProblemIds(Long teamId);

    @Query("SELECT SUM(s.pointsEarned) FROM DebuggingSubmission s WHERE s.teamId = :teamId AND s.id IN " +
           "(SELECT MAX(s2.id) FROM DebuggingSubmission s2 WHERE s2.teamId = :teamId GROUP BY s2.problemId)")
    Integer calculateTotalPoints(Long teamId);
}
