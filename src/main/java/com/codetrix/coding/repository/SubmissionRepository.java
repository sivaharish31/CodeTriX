package com.codetrix.coding.repository;

import com.codetrix.coding.entity.Submission;
import com.codetrix.coding.entity.SubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    List<Submission> findByTeamIdOrderBySubmissionTimeDesc(Long teamId);

    List<Submission> findByTeamIdAndProblemIdOrderBySubmissionTimeDesc(Long teamId, Long problemId);

    List<Submission> findByProblemIdOrderBySubmissionTimeDesc(Long problemId);

    List<Submission> findByStatus(SubmissionStatus status);

    @Query("SELECT s FROM Submission s WHERE s.teamId = :teamId AND s.problemId = :problemId AND s.status = 'ACCEPTED' ORDER BY s.submissionTime DESC LIMIT 1")
    Optional<Submission> findAcceptedSubmission(Long teamId, Long problemId);

    @Query("SELECT s FROM Submission s WHERE s.teamId = :teamId AND s.problemId = :problemId ORDER BY s.pointsEarned DESC LIMIT 1")
    Optional<Submission> findBestSubmission(Long teamId, Long problemId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.teamId = :teamId AND s.problemId = :problemId")
    long countSubmissions(Long teamId, Long problemId);

    @Query("SELECT DISTINCT s.problemId FROM Submission s WHERE s.teamId = :teamId AND s.status = 'ACCEPTED'")
    List<Long> findSolvedProblemIds(Long teamId);

    @Query("SELECT SUM(s.pointsEarned) FROM Submission s WHERE s.teamId = :teamId AND s.id IN " +
           "(SELECT MAX(s2.id) FROM Submission s2 WHERE s2.teamId = :teamId GROUP BY s2.problemId)")
    Integer calculateTotalPoints(Long teamId);
}
