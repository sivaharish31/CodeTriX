package com.codetrix.leaderboard.controller;

import com.codetrix.leaderboard.dto.AdminScoresResponse;
import com.codetrix.leaderboard.service.LeaderboardBroadcaster;
import com.codetrix.leaderboard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/scores")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminScoreController {

    private final LeaderboardService leaderboardService;
    private final LeaderboardBroadcaster leaderboardBroadcaster;

    @GetMapping
    public ResponseEntity<AdminScoresResponse> getAdminScores() {
        return ResponseEntity.ok(leaderboardService.getAdminScores());
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> forceBroadcast() {
        leaderboardBroadcaster.broadcastUpdate();
        return ResponseEntity.ok(Map.of("message", "Leaderboard broadcast triggered"));
    }
}
