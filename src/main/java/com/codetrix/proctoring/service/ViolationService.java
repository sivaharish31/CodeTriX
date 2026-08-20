package com.codetrix.proctoring.service;

import com.codetrix.proctoring.dto.*;
import com.codetrix.proctoring.entity.*;
import com.codetrix.proctoring.repository.TeamReviewStatusRepository;
import com.codetrix.proctoring.repository.ViolationRepository;
import com.codetrix.team.entity.Team;
import com.codetrix.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViolationService {

    private final ViolationRepository violationRepository;
    private final TeamReviewStatusRepository reviewStatusRepository;
    private final TeamRepository teamRepository;
    private final ProctoringBroadcaster broadcaster;

    private static final int DUPLICATE_WINDOW_SECONDS = 2;

    @Transactional
    public ViolationResponse recordViolation(Long teamId, Long roundId, ViolationRequest request, String userAgent) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        LocalDateTime violationTime = request.getClientTimestamp() != null
            ? LocalDateTime.ofInstant(Instant.ofEpochMilli(request.getClientTimestamp()), ZoneId.systemDefault())
            : LocalDateTime.now();

        if (isDuplicateViolation(teamId, request.getViolationType(), violationTime)) {
            log.debug("Duplicate violation ignored for team {}: {}", teamId, request.getViolationType());
            return null;
        }

        Violation violation = Violation.builder()
            .team(team)
            .roundId(roundId != null ? roundId : request.getRoundId())
            .violationType(request.getViolationType())
            .violationTime(violationTime)
            .clientTimestamp(request.getClientTimestamp())
            .details(request.getDetails())
            .userAgent(userAgent)
            .build();

        violation = violationRepository.save(violation);
        log.info("Recorded violation for team {}: {} at {}", teamId, request.getViolationType(), violationTime);

        ensureReviewStatus(team);

        ViolationResponse response = ViolationResponse.from(violation);
        broadcaster.broadcastViolation(response);

        return response;
    }

    private boolean isDuplicateViolation(Long teamId, ViolationType type, LocalDateTime time) {
        LocalDateTime windowStart = time.minusSeconds(DUPLICATE_WINDOW_SECONDS);
        LocalDateTime windowEnd = time.plusSeconds(DUPLICATE_WINDOW_SECONDS);
        return violationRepository.existsByTeamIdAndViolationTypeAndViolationTimeBetween(
            teamId, type, windowStart, windowEnd);
    }

    private void ensureReviewStatus(Team team) {
        if (!reviewStatusRepository.existsByTeamId(team.getId())) {
            TeamReviewStatus status = TeamReviewStatus.builder()
                .team(team)
                .status(ReviewStatus.PENDING)
                .build();
            reviewStatusRepository.save(status);
        }
    }

    @Transactional(readOnly = true)
    public AdminViolationsResponse getAdminViolations(int limit) {
        List<Violation> violations = violationRepository.findRecentViolations(PageRequest.of(0, limit));

        List<ViolationResponse> responses = violations.stream()
            .map(ViolationResponse::from)
            .collect(Collectors.toList());

        Map<String, Integer> byType = violations.stream()
            .collect(Collectors.groupingBy(
                v -> v.getViolationType().name(),
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));

        List<Object[]> teamCounts = violationRepository.getViolationCountsByTeam();
        List<AdminViolationsResponse.TeamViolationCount> topOffenders = teamCounts.stream()
            .limit(10)
            .map(row -> {
                Long teamId = (Long) row[0];
                Integer count = ((Number) row[1]).intValue();
                String teamName = teamRepository.findById(teamId)
                    .map(Team::getTeamName)
                    .orElse("Unknown");
                return AdminViolationsResponse.TeamViolationCount.builder()
                    .teamId(teamId)
                    .teamName(teamName)
                    .count(count)
                    .build();
            })
            .collect(Collectors.toList());

        Set<Long> teamsWithViolations = violations.stream()
            .map(v -> v.getTeam().getId())
            .collect(Collectors.toSet());

        return AdminViolationsResponse.builder()
            .violations(responses)
            .totalViolations((int) violationRepository.count())
            .teamsWithViolations(teamsWithViolations.size())
            .violationsByType(byType)
            .topOffenders(topOffenders)
            .build();
    }

    @Transactional(readOnly = true)
    public TeamViolationSummary getTeamViolations(Long teamId) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        List<Violation> violations = violationRepository.findByTeamIdOrderByViolationTimeDesc(teamId);

        Map<ViolationType, Integer> byType = violations.stream()
            .collect(Collectors.groupingBy(
                Violation::getViolationType,
                Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
            ));

        List<ViolationResponse> recent = violations.stream()
            .limit(20)
            .map(ViolationResponse::from)
            .collect(Collectors.toList());

        TeamReviewStatus reviewStatus = reviewStatusRepository.findByTeamId(teamId)
            .orElse(null);

        LocalDateTime lastViolation = violations.isEmpty() ? null : violations.get(0).getViolationTime();

        return TeamViolationSummary.builder()
            .teamId(teamId)
            .teamName(team.getTeamName())
            .totalViolations(violations.size())
            .violationsByType(byType)
            .recentViolations(recent)
            .reviewStatus(reviewStatus != null ? reviewStatus.getStatus() : ReviewStatus.PENDING)
            .adminNotes(reviewStatus != null ? reviewStatus.getAdminNotes() : null)
            .lastViolationTime(lastViolation)
            .build();
    }

    @Transactional
    public TeamViolationSummary updateReviewStatus(Long teamId, UpdateReviewStatusRequest request, String adminUsername) {
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        TeamReviewStatus status = reviewStatusRepository.findByTeamId(teamId)
            .orElseGet(() -> TeamReviewStatus.builder().team(team).build());

        status.setStatus(request.getStatus());
        if (request.getAdminNotes() != null) {
            status.setAdminNotes(request.getAdminNotes());
        }
        status.setReviewedBy(adminUsername);
        status.setReviewedAt(LocalDateTime.now());

        reviewStatusRepository.save(status);
        log.info("Updated review status for team {} to {} by {}", teamId, request.getStatus(), adminUsername);

        return getTeamViolations(teamId);
    }

    @Transactional(readOnly = true)
    public List<TeamViolationSummary> getTeamsForReview() {
        List<TeamReviewStatus> statuses = reviewStatusRepository.findByStatusIn(
            List.of(ReviewStatus.PENDING, ReviewStatus.UNDER_REVIEW, ReviewStatus.FLAGGED)
        );

        return statuses.stream()
            .map(s -> getTeamViolations(s.getTeam().getId()))
            .filter(s -> s.getTotalViolations() > 0)
            .sorted((a, b) -> b.getTotalViolations().compareTo(a.getTotalViolations()))
            .collect(Collectors.toList());
    }
}
