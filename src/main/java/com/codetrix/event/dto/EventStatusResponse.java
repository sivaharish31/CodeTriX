package com.codetrix.event.dto;

import com.codetrix.event.entity.Event;
import com.codetrix.event.entity.EventStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventStatusResponse {

    private Long eventId;
    private String eventName;
    private EventStatus status;
    private Instant startTime;
    private Instant endTime;
    private Instant serverTime;
    private Long totalRemainingSeconds;
    private Integer currentRoundNumber;
    private List<RoundStatusResponse> rounds;

    public static EventStatusResponse fromEntity(Event event, Instant serverTime) {
        Long remainingSeconds = null;
        if (event.isRunning() && event.getEndTime() != null) {
            remainingSeconds = Math.max(0, event.getEndTime().getEpochSecond() - serverTime.getEpochSecond());
        }

        Integer currentRound = null;
        if (event.getRounds() != null) {
            currentRound = event.getRounds().stream()
                    .filter(r -> r.getStatus() == com.codetrix.event.entity.RoundStatus.RUNNING)
                    .findFirst()
                    .map(r -> r.getRoundNumber())
                    .orElse(null);
        }

        List<RoundStatusResponse> roundResponses = event.getRounds() != null
                ? event.getRounds().stream()
                    .map(r -> RoundStatusResponse.fromEntity(r, serverTime))
                    .toList()
                : List.of();

        return EventStatusResponse.builder()
                .eventId(event.getId())
                .eventName(event.getName())
                .status(event.getStatus())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .serverTime(serverTime)
                .totalRemainingSeconds(remainingSeconds)
                .currentRoundNumber(currentRound)
                .rounds(roundResponses)
                .build();
    }
}
