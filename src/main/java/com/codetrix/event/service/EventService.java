package com.codetrix.event.service;

import com.codetrix.event.dto.*;
import com.codetrix.event.entity.*;
import com.codetrix.event.exception.EventException;
import com.codetrix.event.repository.EventRepository;
import com.codetrix.event.repository.RoundRepository;
import com.codetrix.event.websocket.EventWebSocketService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private static final int ROUND_DURATION_SECONDS = 900; // 15 minutes
    private static final int TOTAL_ROUNDS = 3;
    private static final int BROADCAST_INTERVAL_SECONDS = 1;

    private final EventRepository eventRepository;
    private final RoundRepository roundRepository;
    private final TaskScheduler taskScheduler;
    private final EventWebSocketService webSocketService;

    private final AtomicReference<ScheduledFuture<?>> timerTask = new AtomicReference<>();
    private final AtomicReference<Long> activeEventId = new AtomicReference<>();

    @PostConstruct
    public void initialize() {
        eventRepository.findLatestEventWithRounds().ifPresent(event -> {
            if (event.isRunning()) {
                log.info("Resuming timer for running event: {}", event.getId());
                activeEventId.set(event.getId());
                startTimerBroadcast();
            }
        });
    }

    @Transactional
    public EventStartResponse startEvent() {
        if (eventRepository.hasEventStartedOrCompleted()) {
            throw EventException.eventAlreadyStarted();
        }

        Instant now = Instant.now();
        int totalDuration = ROUND_DURATION_SECONDS * TOTAL_ROUNDS;

        Event event = Event.builder()
                .name("CodeTriX Competition")
                .startTime(now)
                .endTime(now.plusSeconds(totalDuration))
                .status(EventStatus.RUNNING)
                .totalDurationSeconds(totalDuration)
                .build();

        createRounds(event, now);
        event = eventRepository.save(event);

        activeEventId.set(event.getId());
        startTimerBroadcast();

        log.info("Event started: {} at {}", event.getId(), now);

        webSocketService.broadcastEventStart(
                TimerBroadcast.eventStart(now, (long) totalDuration)
        );

        return EventStartResponse.success(
                event.getId(),
                event.getName(),
                event.getStartTime(),
                event.getEndTime(),
                totalDuration
        );
    }

    private void createRounds(Event event, Instant eventStart) {
        RoundType[] roundTypes = {RoundType.CODING, RoundType.DEBUGGING, RoundType.CTF};

        for (int i = 0; i < TOTAL_ROUNDS; i++) {
            Instant roundStart = eventStart.plusSeconds((long) i * ROUND_DURATION_SECONDS);
            Instant roundEnd = roundStart.plusSeconds(ROUND_DURATION_SECONDS);

            Round round = Round.builder()
                    .roundNumber(i + 1)
                    .roundType(roundTypes[i])
                    .startTime(roundStart)
                    .endTime(roundEnd)
                    .durationSeconds(ROUND_DURATION_SECONDS)
                    .status(i == 0 ? RoundStatus.RUNNING : RoundStatus.LOCKED)
                    .build();

            event.addRound(round);
        }
    }

    private void startTimerBroadcast() {
        ScheduledFuture<?> existing = timerTask.get();
        if (existing != null && !existing.isCancelled()) {
            return;
        }

        ScheduledFuture<?> task = taskScheduler.scheduleAtFixedRate(
                this::processTimerTick,
                java.time.Duration.ofSeconds(BROADCAST_INTERVAL_SECONDS)
        );
        timerTask.set(task);
        log.info("Timer broadcast started");
    }

    private void processTimerTick() {
        try {
            Long eventId = activeEventId.get();
            if (eventId == null) {
                return;
            }

            Event event = eventRepository.findByIdWithRounds(eventId).orElse(null);
            if (event == null || event.isCompleted()) {
                stopTimerBroadcast();
                return;
            }

            Instant now = Instant.now();
            processRoundTransitions(event, now);
            broadcastCurrentState(event, now);

        } catch (Exception e) {
            log.error("Error in timer tick: {}", e.getMessage(), e);
        }
    }

    @Transactional
    protected void processRoundTransitions(Event event, Instant now) {
        if (now.isAfter(event.getEndTime()) || now.equals(event.getEndTime())) {
            completeEvent(event, now);
            return;
        }

        List<Round> rounds = event.getRounds();
        Round currentRunning = null;
        Round nextLocked = null;

        for (Round round : rounds) {
            if (round.isRunning()) {
                currentRunning = round;
            } else if (round.isLocked() && nextLocked == null) {
                nextLocked = round;
            }
        }

        if (currentRunning != null && now.isAfter(currentRunning.getEndTime())) {
            currentRunning.setStatus(RoundStatus.COMPLETED);
            roundRepository.save(currentRunning);
            log.info("Round {} completed", currentRunning.getRoundNumber());

            if (nextLocked != null) {
                nextLocked.setStatus(RoundStatus.RUNNING);
                roundRepository.save(nextLocked);
                log.info("Round {} started: {}", nextLocked.getRoundNumber(), nextLocked.getRoundType());

                webSocketService.broadcastRoundChange(
                        TimerBroadcast.roundChange(
                                now,
                                nextLocked.getRoundNumber(),
                                nextLocked.getRoundType(),
                                (long) nextLocked.getDurationSeconds()
                        )
                );
            }
        }
    }

    private void completeEvent(Event event, Instant now) {
        event.setStatus(EventStatus.COMPLETED);
        event.getRounds().forEach(r -> {
            if (!r.isCompleted()) {
                r.setStatus(RoundStatus.COMPLETED);
            }
        });
        eventRepository.save(event);
        activeEventId.set(null);

        log.info("Event completed: {}", event.getId());
        webSocketService.broadcastEventEnd(TimerBroadcast.eventEnd(now));
        stopTimerBroadcast();
    }

    private void broadcastCurrentState(Event event, Instant now) {
        if (event.isCompleted()) {
            return;
        }

        Round currentRound = event.getRounds().stream()
                .filter(Round::isRunning)
                .findFirst()
                .orElse(null);

        long eventRemaining = Math.max(0, event.getEndTime().getEpochSecond() - now.getEpochSecond());
        long roundRemaining = currentRound != null
                ? Math.max(0, currentRound.getEndTime().getEpochSecond() - now.getEpochSecond())
                : 0;

        TimerBroadcast tick = TimerBroadcast.tick(
                now,
                event.getStatus(),
                eventRemaining,
                currentRound != null ? currentRound.getRoundNumber() : null,
                currentRound != null ? currentRound.getRoundType() : null,
                currentRound != null ? currentRound.getStatus() : null,
                roundRemaining
        );

        webSocketService.broadcastTimerTick(tick);
    }

    private void stopTimerBroadcast() {
        ScheduledFuture<?> task = timerTask.getAndSet(null);
        if (task != null) {
            task.cancel(false);
            log.info("Timer broadcast stopped");
        }
    }

    @Transactional(readOnly = true)
    public EventStatusResponse getEventStatus() {
        Event event = eventRepository.findLatestEventWithRounds()
                .orElseThrow(EventException::eventNotFound);

        return EventStatusResponse.fromEntity(event, Instant.now());
    }

    @Transactional(readOnly = true)
    public TimeResponse getTime() {
        Instant now = Instant.now();

        Event event = eventRepository.findLatestEventWithRounds().orElse(null);
        if (event == null) {
            return TimeResponse.notStarted(now);
        }

        if (event.isCompleted()) {
            return TimeResponse.completed(now);
        }

        if (!event.isRunning()) {
            return TimeResponse.notStarted(now);
        }

        Round currentRound = event.getRounds().stream()
                .filter(Round::isRunning)
                .findFirst()
                .orElse(null);

        long eventRemaining = Math.max(0, event.getEndTime().getEpochSecond() - now.getEpochSecond());
        long roundRemaining = currentRound != null
                ? Math.max(0, currentRound.getEndTime().getEpochSecond() - now.getEpochSecond())
                : 0;

        return TimeResponse.builder()
                .serverTime(now)
                .eventStatus(event.getStatus())
                .eventRemainingSeconds(eventRemaining)
                .currentRoundNumber(currentRound != null ? currentRound.getRoundNumber() : null)
                .currentRoundType(currentRound != null ? currentRound.getRoundType() : null)
                .currentRoundStatus(currentRound != null ? currentRound.getStatus() : null)
                .roundRemainingSeconds(roundRemaining)
                .roundEndTime(currentRound != null ? currentRound.getEndTime() : null)
                .build();
    }

    @Transactional(readOnly = true)
    public CurrentRoundResponse getCurrentRound() {
        Instant now = Instant.now();

        Event event = eventRepository.findLatestEventWithRounds().orElse(null);
        if (event == null || !event.isRunning()) {
            return CurrentRoundResponse.noActiveRound(now);
        }

        Round currentRound = event.getRounds().stream()
                .filter(Round::isRunning)
                .findFirst()
                .orElse(null);

        if (currentRound == null) {
            return CurrentRoundResponse.noActiveRound(now);
        }

        return CurrentRoundResponse.fromRound(currentRound, now);
    }

    public boolean isSubmissionAllowed(RoundType roundType) {
        Instant now = Instant.now();

        Event event = eventRepository.findLatestEventWithRounds().orElse(null);
        if (event == null || !event.isRunning()) {
            return false;
        }

        Round currentRound = event.getRounds().stream()
                .filter(Round::isRunning)
                .filter(r -> r.getRoundType() == roundType)
                .findFirst()
                .orElse(null);

        if (currentRound == null) {
            return false;
        }

        return now.isBefore(currentRound.getEndTime());
    }

    public void validateSubmission(RoundType roundType) {
        if (!isSubmissionAllowed(roundType)) {
            throw EventException.submissionAfterDeadline();
        }
    }
}
