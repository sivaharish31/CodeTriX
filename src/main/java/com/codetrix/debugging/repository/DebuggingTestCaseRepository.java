package com.codetrix.debugging.repository;

import com.codetrix.debugging.entity.DebuggingTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebuggingTestCaseRepository extends JpaRepository<DebuggingTestCase, Long> {

    List<DebuggingTestCase> findByProblemIdOrderByDisplayOrderAsc(Long problemId);

    List<DebuggingTestCase> findByProblemIdAndIsSampleTrueOrderByDisplayOrderAsc(Long problemId);

    void deleteByProblemId(Long problemId);

    long countByProblemId(Long problemId);
}
