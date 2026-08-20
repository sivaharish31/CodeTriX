package com.codetrix.event.repository;

import com.codetrix.event.entity.Event;
import com.codetrix.event.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findFirstByOrderByIdDesc();

    Optional<Event> findByStatus(EventStatus status);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.rounds WHERE e.id = :id")
    Optional<Event> findByIdWithRounds(Long id);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.rounds ORDER BY e.id DESC LIMIT 1")
    Optional<Event> findLatestEventWithRounds();

    boolean existsByStatus(EventStatus status);

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.status IN ('RUNNING', 'COMPLETED')")
    boolean hasEventStartedOrCompleted();
}
