package com.codetrix.debugging.repository;

import com.codetrix.debugging.entity.DebuggingProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebuggingProblemRepository extends JpaRepository<DebuggingProblem, Long> {

    List<DebuggingProblem> findByEnabledTrueOrderByDisplayOrderAsc();

    @Query("SELECT p FROM DebuggingProblem p LEFT JOIN FETCH p.testCases WHERE p.id = :id")
    Optional<DebuggingProblem> findByIdWithTestCases(Long id);

    @Query("SELECT p FROM DebuggingProblem p LEFT JOIN FETCH p.testCases WHERE p.enabled = true ORDER BY p.displayOrder ASC")
    List<DebuggingProblem> findAllEnabledWithTestCases();

    @Query("SELECT p FROM DebuggingProblem p LEFT JOIN FETCH p.testCases ORDER BY p.displayOrder ASC")
    List<DebuggingProblem> findAllWithTestCases();

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);
}
