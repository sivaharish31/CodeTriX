package com.codetrix.event.dto;

import com.codetrix.event.entity.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventStartResponse {

    private Long eventId;
    private String eventName;
    private EventStatus status;
    private Instant startTime;
    private Instant endTime;
    private Instant serverTime;
    private Integer totalDurationSeconds;
    private String message;
    private Integer currentRoundNumber;
    private String currentRoundType;

    public static EventStartResponse success(Long eventId, String eventName, Instant startTime,
                                             Instant endTime, Integer totalDuration) {
        return EventStartResponse.builder()
                .eventId(eventId)
                .eventName(eventName)
                .status(EventStatus.RUNNING)
                .startTime(startTime)
                .endTime(endTime)
                .serverTime(Instant.now())
                .totalDurationSeconds(totalDuration)
                .currentRoundNumber(1)
                .currentRoundType("CODING")
                .message("Event started successfully. Timer is now running.")
                .build();
    }
}
