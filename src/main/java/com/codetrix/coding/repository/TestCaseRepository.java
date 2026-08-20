package com.codetrix.coding.repository;

import com.codetrix.coding.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Long> {

    List<TestCase> findByProblemIdOrderByDisplayOrderAsc(Long problemId);

    List<TestCase> findByProblemIdAndIsSampleTrueOrderByDisplayOrderAsc(Long problemId);

    List<TestCase> findByProblemIdAndIsSampleFalseOrderByDisplayOrderAsc(Long problemId);

    void deleteByProblemId(Long problemId);

    long countByProblemId(Long problemId);

    long countByProblemIdAndIsSampleFalse(Long problemId);
}
