package com.codetrix.proctoring.repository;

import com.codetrix.proctoring.entity.Violation;
import com.codetrix.proctoring.entity.ViolationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    List<Violation> findByTeamIdOrderByViolationTimeDesc(Long teamId);

    List<Violation> findByTeamIdAndRoundIdOrderByViolationTimeDesc(Long teamId, Long roundId);

    List<Violation> findByRoundIdOrderByViolationTimeDesc(Long roundId);

    Page<Violation> findAllByOrderByViolationTimeDesc(Pageable pageable);

    @Query("SELECT v FROM Violation v JOIN FETCH v.team ORDER BY v.violationTime DESC")
    List<Violation> findRecentViolations(Pageable pageable);

    @Query("SELECT COUNT(v) FROM Violation v WHERE v.team.id = :teamId")
    int countByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT COUNT(v) FROM Violation v WHERE v.team.id = :teamId AND v.roundId = :roundId")
    int countByTeamIdAndRoundId(@Param("teamId") Long teamId, @Param("roundId") Long roundId);

    @Query("SELECT v.violationType, COUNT(v) FROM Violation v WHERE v.team.id = :teamId GROUP BY v.violationType")
    List<Object[]> countByTeamGroupedByType(@Param("teamId") Long teamId);

    @Query("SELECT DISTINCT v.team.id FROM Violation v WHERE v.violationTime > :since")
    List<Long> findTeamIdsWithRecentViolations(@Param("since") LocalDateTime since);

    @Query("""
        SELECT v.team.id, COUNT(v) FROM Violation v
        GROUP BY v.team.id
        ORDER BY COUNT(v) DESC
        """)
    List<Object[]> getViolationCountsByTeam();

    boolean existsByTeamIdAndViolationTypeAndViolationTimeBetween(
        Long teamId, ViolationType type, LocalDateTime start, LocalDateTime end);
}
