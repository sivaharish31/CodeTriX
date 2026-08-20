package com.codetrix.leaderboard.controller;

import com.codetrix.leaderboard.dto.LeaderboardResponse;
import com.codetrix.leaderboard.dto.TeamScoreResponse;
import com.codetrix.leaderboard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LeaderboardResponse> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getLeaderboard());
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TeamScoreResponse> getTeamScore(@PathVariable Long teamId) {
        Long currentTeamId = getCurrentTeamId();

        if (currentTeamId != null && !currentTeamId.equals(teamId) && !isAdmin()) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(leaderboardService.getTeamScore(teamId));
    }

    @GetMapping("/my-score")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<TeamScoreResponse> getMyScore() {
        Long teamId = getCurrentTeamId();
        if (teamId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(leaderboardService.getTeamScore(teamId));
    }

    private Long getCurrentTeamId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.codetrix.auth.security.UserPrincipal principal) {
            return principal.getTeamId();
        }
        return null;
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }
}
