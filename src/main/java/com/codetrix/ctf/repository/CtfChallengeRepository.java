package com.codetrix.ctf.repository;

import com.codetrix.ctf.entity.CtfCategory;
import com.codetrix.ctf.entity.CtfChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CtfChallengeRepository extends JpaRepository<CtfChallenge, Long> {

    List<CtfChallenge> findByActiveTrue();

    List<CtfChallenge> findByActiveTrueOrderByPointsAsc();

    List<CtfChallenge> findByCategoryAndActiveTrue(CtfCategory category);

    List<CtfChallenge> findAllByOrderByPointsAsc();

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);
}
