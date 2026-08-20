package com.codetrix.coding.repository;

import com.codetrix.coding.entity.CodingProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodingProblemRepository extends JpaRepository<CodingProblem, Long> {

    List<CodingProblem> findByEnabledTrueOrderByDisplayOrderAsc();

    @Query("SELECT p FROM CodingProblem p LEFT JOIN FETCH p.testCases WHERE p.id = :id")
    Optional<CodingProblem> findByIdWithTestCases(Long id);

    @Query("SELECT p FROM CodingProblem p LEFT JOIN FETCH p.testCases WHERE p.enabled = true ORDER BY p.displayOrder ASC")
    List<CodingProblem> findAllEnabledWithTestCases();

    @Query("SELECT p FROM CodingProblem p LEFT JOIN FETCH p.testCases ORDER BY p.displayOrder ASC")
    List<CodingProblem> findAllWithTestCases();

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);
}
