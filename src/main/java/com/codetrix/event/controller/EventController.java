package com.codetrix.event.controller;

import com.codetrix.event.dto.*;
import com.codetrix.event.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping("/admin/event/start")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventStartResponse> startEvent() {
        log.info("Admin request to start event");
        EventStartResponse response = eventService.startEvent();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/event/status")
    public ResponseEntity<EventStatusResponse> getEventStatus() {
        EventStatusResponse response = eventService.getEventStatus();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/time")
    public ResponseEntity<TimeResponse> getTime() {
        TimeResponse response = eventService.getTime();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event/current-round")
    public ResponseEntity<CurrentRoundResponse> getCurrentRound() {
        CurrentRoundResponse response = eventService.getCurrentRound();
        return ResponseEntity.ok(response);
    }
}
