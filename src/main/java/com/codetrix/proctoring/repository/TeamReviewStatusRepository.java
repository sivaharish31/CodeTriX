package com.codetrix.proctoring.repository;

import com.codetrix.proctoring.entity.ReviewStatus;
import com.codetrix.proctoring.entity.TeamReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamReviewStatusRepository extends JpaRepository<TeamReviewStatus, Long> {

    Optional<TeamReviewStatus> findByTeamId(Long teamId);

    List<TeamReviewStatus> findByStatus(ReviewStatus status);

    List<TeamReviewStatus> findByStatusIn(List<ReviewStatus> statuses);

    @Query("SELECT t FROM TeamReviewStatus t JOIN FETCH t.team ORDER BY t.updatedAt DESC")
    List<TeamReviewStatus> findAllWithTeam();

    boolean existsByTeamId(Long teamId);
}
