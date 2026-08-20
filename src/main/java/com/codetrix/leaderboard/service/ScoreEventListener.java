package com.codetrix.leaderboard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreEventListener {

    private final ScoreService scoreService;

    @Async
    @EventListener
    public void handleCodingSubmission(CodingSubmissionEvent event) {
        log.debug("Received coding submission event for team {}, problem {}",
            event.teamId(), event.problemId());
        scoreService.recordCodingScore(
            event.teamId(),
            event.problemId(),
            event.submissionId(),
            event.testsPassed(),
            event.totalTests(),
            event.maxPoints()
        );
    }

    @Async
    @EventListener
    public void handleDebuggingSubmission(DebuggingSubmissionEvent event) {
        log.debug("Received debugging submission event for team {}, problem {}",
            event.teamId(), event.problemId());
        scoreService.recordDebuggingScore(
            event.teamId(),
            event.problemId(),
            event.submissionId(),
            event.testsPassed(),
            event.totalTests(),
            event.maxPoints()
        );
    }

    @Async
    @EventListener
    public void handleCtfSubmission(CtfSubmissionEvent event) {
        log.debug("Received CTF submission event for team {}, challenge {}",
            event.teamId(), event.challengeId());
        scoreService.recordCtfScore(
            event.teamId(),
            event.challengeId(),
            event.submissionId(),
            event.points()
        );
    }

    public record CodingSubmissionEvent(
        Long teamId,
        Long problemId,
        Long submissionId,
        int testsPassed,
        int totalTests,
        int maxPoints
    ) {}

    public record DebuggingSubmissionEvent(
        Long teamId,
        Long problemId,
        Long submissionId,
        int testsPassed,
        int totalTests,
        int maxPoints
    ) {}

    public record CtfSubmissionEvent(
        Long teamId,
        Long challengeId,
        Long submissionId,
        int points
    ) {}
}
