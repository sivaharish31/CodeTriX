package com.codetrix.proctoring.service;

import com.codetrix.proctoring.dto.ViolationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProctoringBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public void broadcastViolation(ViolationResponse violation) {
        try {
            messagingTemplate.convertAndSend("/topic/admin/violations", violation);
            log.debug("Broadcast violation to admin: team={}, type={}",
                violation.getTeamId(), violation.getViolationType());
        } catch (Exception e) {
            log.error("Failed to broadcast violation: {}", e.getMessage());
        }
    }

    public void sendWarningToTeam(Long teamId, String message) {
        try {
            messagingTemplate.convertAndSend("/topic/team/" + teamId + "/warning", message);
        } catch (Exception e) {
            log.error("Failed to send warning to team {}: {}", teamId, e.getMessage());
        }
    }
}
