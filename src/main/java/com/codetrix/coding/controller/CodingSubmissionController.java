package com.codetrix.coding.controller;

import com.codetrix.coding.dto.*;
import com.codetrix.coding.service.SubmissionService;
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
@RequestMapping("/api/coding")
@RequiredArgsConstructor
public class CodingSubmissionController {

    private final SubmissionService submissionService;
    private final TeamRepository teamRepository;

    @PostMapping("/run")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<RunCodeResponse> runCode(
            Authentication authentication,
            @Valid @RequestBody RunCodeRequest request) {
        Team team = getTeamFromAuth(authentication);
        log.info("Team {} running code for problem {}", team.getTeamCode(), request.getProblemId());

        RunCodeResponse response = submissionService.runCode(team.getId(), request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<SubmissionResponse> submitCode(
            Authentication authentication,
            @Valid @RequestBody SubmitCodeRequest request) {
        Team team = getTeamFromAuth(authentication);
        log.info("Team {} submitting code for problem {}", team.getTeamCode(), request.getProblemId());

        SubmissionResponse response = submissionService.submitCode(
                team.getId(),
                team.getTeamCode(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/submissions")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<SubmissionListResponse> getSubmissions(Authentication authentication) {
        Team team = getTeamFromAuth(authentication);
        SubmissionListResponse response = submissionService.getSubmissionsForTeam(team.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/submissions/{id}")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<SubmissionResponse> getSubmission(
            Authentication authentication,
            @PathVariable Long id) {
        Team team = getTeamFromAuth(authentication);
        SubmissionResponse response = submissionService.getSubmission(id, team.getId());
        return ResponseEntity.ok(response);
    }

    private Team getTeamFromAuth(Authentication authentication) {
        String teamCode = authentication.getName();
        return teamRepository.findByTeamCode(teamCode)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamCode));
    }
}
