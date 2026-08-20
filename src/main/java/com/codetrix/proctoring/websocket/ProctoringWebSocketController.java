package com.codetrix.proctoring.websocket;

import com.codetrix.proctoring.dto.AdminViolationsResponse;
import com.codetrix.proctoring.service.ViolationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProctoringWebSocketController {

    private final ViolationService violationService;

    @MessageMapping("/proctoring/refresh")
    @SendTo("/topic/admin/violations/summary")
    public AdminViolationsResponse refreshViolations() {
        log.debug("Violations refresh requested via WebSocket");
        return violationService.getAdminViolations(50);
    }
}
