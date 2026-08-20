package com.codetrix.event.repository;

import com.codetrix.event.entity.Round;
import com.codetrix.event.entity.RoundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {

    List<Round> findByEventIdOrderByRoundNumberAsc(Long eventId);

    Optional<Round> findByEventIdAndRoundNumber(Long eventId, Integer roundNumber);

    Optional<Round> findByEventIdAndStatus(Long eventId, RoundStatus status);

    @Query("SELECT r FROM Round r WHERE r.event.id = :eventId AND r.status = 'RUNNING'")
    Optional<Round> findCurrentRound(Long eventId);

    @Query("SELECT r FROM Round r WHERE r.event.id = :eventId ORDER BY r.roundNumber ASC")
    List<Round> findAllByEventId(Long eventId);
}
