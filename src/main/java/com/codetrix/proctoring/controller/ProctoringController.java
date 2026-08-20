package com.codetrix.proctoring.controller;

import com.codetrix.proctoring.dto.ViolationRequest;
import com.codetrix.proctoring.dto.ViolationResponse;
import com.codetrix.proctoring.service.ViolationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/proctoring")
@RequiredArgsConstructor
public class ProctoringController {

    private final ViolationService violationService;

    @PostMapping("/violations")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<?> reportViolation(
            @Valid @RequestBody ViolationRequest request,
            HttpServletRequest httpRequest) {

        Long teamId = getCurrentTeamId();
        if (teamId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Team not identified"));
        }

        Long roundId = request.getRoundId();
        String userAgent = httpRequest.getHeader("User-Agent");

        ViolationResponse response = violationService.recordViolation(teamId, roundId, request, userAgent);

        if (response == null) {
            return ResponseEntity.ok(Map.of("status", "duplicate", "message", "Violation already recorded"));
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/violations/batch")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<?> reportViolationsBatch(
            @Valid @RequestBody java.util.List<ViolationRequest> requests,
            HttpServletRequest httpRequest) {

        Long teamId = getCurrentTeamId();
        if (teamId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Team not identified"));
        }

        String userAgent = httpRequest.getHeader("User-Agent");

        java.util.List<ViolationResponse> responses = new java.util.ArrayList<>();
        for (ViolationRequest request : requests) {
            ViolationResponse response = violationService.recordViolation(
                teamId, request.getRoundId(), request, userAgent);
            if (response != null) {
                responses.add(response);
            }
        }

        return ResponseEntity.ok(Map.of(
            "recorded", responses.size(),
            "total", requests.size()
        ));
    }

    private Long getCurrentTeamId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.codetrix.auth.security.UserPrincipal principal) {
            return principal.getTeamId();
        }
        return null;
    }
}
