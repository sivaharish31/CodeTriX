package com.codetrix.ctf.service;

import com.codetrix.ctf.dto.*;
import com.codetrix.ctf.entity.CtfChallenge;
import com.codetrix.ctf.entity.CtfSubmission;
import com.codetrix.ctf.exception.CtfException;
import com.codetrix.ctf.repository.CtfChallengeRepository;
import com.codetrix.ctf.repository.CtfSubmissionRepository;
import com.codetrix.event.entity.RoundType;
import com.codetrix.event.service.EventService;
import com.codetrix.team.entity.Team;
import com.codetrix.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CtfSubmissionService {

    private final CtfSubmissionRepository submissionRepository;
    private final CtfChallengeRepository challengeRepository;
    private final TeamRepository teamRepository;
    private final EventService eventService;

    @Value("${ctf.rate-limit.attempts:5}")
    private int maxAttemptsPerMinute;

    @Value("${ctf.rate-limit.window-seconds:60}")
    private int rateLimitWindowSeconds;

    @Transactional
    public CtfSubmitResponse submitFlag(Long teamId, CtfSubmitRequest request) {
        validateCtfRoundActive();

        CtfChallenge challenge = challengeRepository.findById(request.getChallengeId())
            .orElseThrow(() -> CtfException.challengeNotFound(request.getChallengeId()));

        if (!challenge.getActive()) {
            throw CtfException.challengeNotActive(request.getChallengeId());
        }

        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        if (submissionRepository.existsByTeamIdAndChallengeIdAndCorrectTrue(teamId, challenge.getId())) {
            return CtfSubmitResponse.alreadySolved(challenge.getId(), challenge.getTitle(), challenge.getPoints());
        }

        checkRateLimit(teamId, challenge.getId());

        String submittedFlag = normalizeFlag(request.getFlag());
        boolean isCorrect = challenge.getFlag().equals(submittedFlag);

        CtfSubmission submission = CtfSubmission.builder()
            .challenge(challenge)
            .team(team)
            .submittedFlag(submittedFlag)
            .correct(isCorrect)
            .pointsAwarded(isCorrect ? challenge.getPoints() : 0)
            .submissionTime(LocalDateTime.now())
            .build();

        submission = submissionRepository.save(submission);

        if (isCorrect) {
            log.info("Team {} solved CTF challenge: {} (+{} points)",
                team.getTeamName(), challenge.getTitle(), challenge.getPoints());
            return CtfSubmitResponse.correct(
                submission.getId(),
                challenge.getId(),
                challenge.getTitle(),
                challenge.getPoints()
            );
        } else {
            log.debug("Team {} incorrect flag for: {}", team.getTeamName(), challenge.getTitle());
            return CtfSubmitResponse.incorrect(
                submission.getId(),
                challenge.getId(),
                challenge.getTitle()
            );
        }
    }

    public CtfSubmissionListResponse getSubmissions(Long teamId) {
        List<CtfSubmission> submissions = submissionRepository.findByTeamIdOrderBySubmissionTimeDesc(teamId);

        List<CtfSubmissionResponse> submissionResponses = submissions.stream()
            .map(CtfSubmissionResponse::from)
            .collect(Collectors.toList());

        Integer totalPoints = submissionRepository.getTotalPointsByTeamId(teamId);
        Integer solvedCount = submissionRepository.countSolvedChallengesByTeamId(teamId);
        long totalChallenges = challengeRepository.count();

        return CtfSubmissionListResponse.builder()
            .submissions(submissionResponses)
            .totalPoints(totalPoints != null ? totalPoints : 0)
            .challengesSolved(solvedCount != null ? solvedCount : 0)
            .totalChallenges((int) totalChallenges)
            .build();
    }

    private void validateCtfRoundActive() {
        if (!eventService.isSubmissionAllowed(RoundType.CTF)) {
            throw CtfException.roundNotActive();
        }
    }

    private void checkRateLimit(Long teamId, Long challengeId) {
        LocalDateTime since = LocalDateTime.now().minusSeconds(rateLimitWindowSeconds);
        long recentAttempts = submissionRepository.countRecentAttempts(teamId, challengeId, since);

        if (recentAttempts >= maxAttemptsPerMinute) {
            log.warn("Rate limit exceeded for team {} on challenge {}", teamId, challengeId);
            throw CtfException.rateLimitExceeded();
        }
    }

    private String normalizeFlag(String flag) {
        return flag.trim();
    }
}
