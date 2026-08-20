package com.codetrix.team.controller;

import com.codetrix.team.dto.*;
import com.codetrix.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/teams")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TeamManagementController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<TeamCredentialsResponse> createTeam(@Valid @RequestBody CreateTeamRequest request) {
        log.info("Request to create team: {}", request.getTeamName());
        TeamCredentialsResponse response = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<TeamListResponse> getAllTeams() {
        log.debug("Request to get all teams");
        TeamListResponse response = teamService.getAllTeams();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<TeamResponse> getTeamById(@PathVariable Long teamId) {
        log.debug("Request to get team: {}", teamId);
        TeamResponse response = teamService.getTeamById(teamId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody UpdateTeamRequest request) {
        log.info("Request to update team: {}", teamId);
        TeamResponse response = teamService.updateTeam(teamId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{teamId}/credentials")
    public ResponseEntity<TeamCredentialsResponse> regenerateCredentials(@PathVariable Long teamId) {
        log.info("Request to regenerate credentials for team: {}", teamId);
        TeamCredentialsResponse response = teamService.regenerateCredentials(teamId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<MemberResponse> addMember(
            @PathVariable Long teamId,
            @Valid @RequestBody AddMemberRequest request) {
        log.info("Request to add member to team: {}", teamId);
        MemberResponse response = teamService.addMember(teamId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<MemberResponse> updateMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId,
            @Valid @RequestBody UpdateMemberRequest request) {
        log.info("Request to update member {} in team {}", memberId, teamId);
        MemberResponse response = teamService.updateMember(teamId, memberId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{teamId}/members/{memberId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long teamId,
            @PathVariable Long memberId) {
        log.info("Request to remove member {} from team {}", memberId, teamId);
        teamService.removeMember(teamId, memberId);
        return ResponseEntity.noContent().build();
    }
}
