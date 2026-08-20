package com.codetrix.leaderboard.service;

import com.codetrix.leaderboard.dto.LeaderboardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LeaderboardBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;
    private final LeaderboardService leaderboardService;

    public LeaderboardBroadcaster(SimpMessagingTemplate messagingTemplate,
                                   @Lazy LeaderboardService leaderboardService) {
        this.messagingTemplate = messagingTemplate;
        this.leaderboardService = leaderboardService;
    }

    @Async
    public void broadcastUpdate() {
        try {
            LeaderboardResponse leaderboard = leaderboardService.getLeaderboard();
            messagingTemplate.convertAndSend("/topic/leaderboard", leaderboard);
            log.debug("Broadcast leaderboard update to {} teams", leaderboard.getTotalTeams());
        } catch (Exception e) {
            log.error("Failed to broadcast leaderboard update: {}", e.getMessage());
        }
    }

    public void broadcastToTeam(Long teamId, Object message) {
        try {
            messagingTemplate.convertAndSend("/topic/team/" + teamId, message);
        } catch (Exception e) {
            log.error("Failed to broadcast to team {}: {}", teamId, e.getMessage());
        }
    }
}
