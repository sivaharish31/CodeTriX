package com.codetrix.leaderboard.websocket;

import com.codetrix.leaderboard.dto.LeaderboardResponse;
import com.codetrix.leaderboard.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LeaderboardWebSocketController {

    private final LeaderboardService leaderboardService;

    @MessageMapping("/leaderboard/refresh")
    @SendTo("/topic/leaderboard")
    public LeaderboardResponse refreshLeaderboard() {
        log.debug("Leaderboard refresh requested via WebSocket");
        return leaderboardService.getLeaderboard();
    }
}
