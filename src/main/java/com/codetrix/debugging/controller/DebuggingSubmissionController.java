package com.codetrix.debugging.controller;

import com.codetrix.debugging.dto.*;
import com.codetrix.debugging.service.DebuggingSubmissionService;
import com.codetrix.team.entity.Team;
import com.codetrix.team.repository.TeamRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/debugging")
@RequiredArgsConstructor
public class DebuggingSubmissionController {

    private final DebuggingSubmissionService submissionService;
    private final TeamRepository teamRepository;

    @PostMapping("/run")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<DebuggingRunResponse> runCode(
            Authentication authentication,
            @Valid @RequestBody DebuggingRunRequest request) {
        Team team = getTeamFromAuth(authentication);
        log.info("Team {} running debugging code for problem {}", team.getTeamCode(), request.getProblemId());

        DebuggingRunResponse response = submissionService.runCode(team.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<DebuggingSubmissionResponse> submitCode(
            Authentication authentication,
            @Valid @RequestBody DebuggingSubmitRequest request) {
        Team team = getTeamFromAuth(authentication);
        log.info("Team {} submitting debugging code for problem {}", team.getTeamCode(), request.getProblemId());

        DebuggingSubmissionResponse response = submissionService.submitCode(
                team.getId(),
                team.getTeamCode(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/submissions")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<DebuggingSubmissionListResponse> getSubmissions(Authentication authentication) {
        Team team = getTeamFromAuth(authentication);
        DebuggingSubmissionListResponse response = submissionService.getSubmissionsForTeam(team.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/submissions/{id}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<DebuggingSubmissionResponse> getSubmission(
            Authentication authentication,
            @PathVariable Long id) {
        Team team = getTeamFromAuth(authentication);
        DebuggingSubmissionResponse response = submissionService.getSubmission(id, team.getId());
        return ResponseEntity.ok(response);
    }

    private Team getTeamFromAuth(Authentication authentication) {
        String teamCode = authentication.getName();
        return teamRepository.findByTeamCode(teamCode)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamCode));
    }
}
