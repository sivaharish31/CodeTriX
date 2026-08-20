package com.codetrix.proctoring.controller;

import com.codetrix.proctoring.dto.*;
import com.codetrix.proctoring.service.ViolationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/proctoring")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminProctoringController {

    private final ViolationService violationService;

    @GetMapping("/violations")
    public ResponseEntity<AdminViolationsResponse> getViolations(
            @RequestParam(defaultValue = "100") int limit) {
        return ResponseEntity.ok(violationService.getAdminViolations(limit));
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<TeamViolationSummary> getTeamViolations(@PathVariable Long teamId) {
        return ResponseEntity.ok(violationService.getTeamViolations(teamId));
    }

    @PutMapping("/teams/{teamId}/review")
    public ResponseEntity<TeamViolationSummary> updateReviewStatus(
            @PathVariable Long teamId,
            @Valid @RequestBody UpdateReviewStatusRequest request) {
        String adminUsername = getCurrentUsername();
        return ResponseEntity.ok(violationService.updateReviewStatus(teamId, request, adminUsername));
    }

    @GetMapping("/review-queue")
    public ResponseEntity<List<TeamViolationSummary>> getReviewQueue() {
        return ResponseEntity.ok(violationService.getTeamsForReview());
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "unknown";
    }
}
