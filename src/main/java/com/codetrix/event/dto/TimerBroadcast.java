package com.codetrix.event.dto;

import com.codetrix.event.entity.EventStatus;
import com.codetrix.event.entity.RoundStatus;
import com.codetrix.event.entity.RoundType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerBroadcast {

    private String type; // TICK, ROUND_CHANGE, EVENT_START, EVENT_END
    private Instant serverTime;
    private EventStatus eventStatus;
    private Long eventRemainingSeconds;
    private Integer currentRoundNumber;
    private RoundType currentRoundType;
    private RoundStatus currentRoundStatus;
    private Long roundRemainingSeconds;
    private String message;

    public static TimerBroadcast tick(Instant serverTime, EventStatus eventStatus,
                                      Long eventRemaining, Integer roundNumber,
                                      RoundType roundType, RoundStatus roundStatus,
                                      Long roundRemaining) {
        return TimerBroadcast.builder()
                .type("TICK")
                .serverTime(serverTime)
                .eventStatus(eventStatus)
                .eventRemainingSeconds(eventRemaining)
                .currentRoundNumber(roundNumber)
                .currentRoundType(roundType)
                .currentRoundStatus(roundStatus)
                .roundRemainingSeconds(roundRemaining)
                .build();
    }

    public static TimerBroadcast roundChange(Instant serverTime, Integer newRoundNumber,
                                             RoundType newRoundType, Long roundDuration) {
        return TimerBroadcast.builder()
                .type("ROUND_CHANGE")
                .serverTime(serverTime)
                .currentRoundNumber(newRoundNumber)
                .currentRoundType(newRoundType)
                .currentRoundStatus(RoundStatus.RUNNING)
                .roundRemainingSeconds(roundDuration)
                .message("Round " + newRoundNumber + " (" + newRoundType + ") has started!")
                .build();
    }

    public static TimerBroadcast eventStart(Instant serverTime, Long totalDuration) {
        return TimerBroadcast.builder()
                .type("EVENT_START")
                .serverTime(serverTime)
                .eventStatus(EventStatus.RUNNING)
                .eventRemainingSeconds(totalDuration)
                .currentRoundNumber(1)
                .currentRoundType(RoundType.CODING)
                .currentRoundStatus(RoundStatus.RUNNING)
                .roundRemainingSeconds(900L)
                .message("Event has started! Good luck!")
                .build();
    }

    public static TimerBroadcast eventEnd(Instant serverTime) {
        return TimerBroadcast.builder()
                .type("EVENT_END")
                .serverTime(serverTime)
                .eventStatus(EventStatus.COMPLETED)
                .eventRemainingSeconds(0L)
                .roundRemainingSeconds(0L)
                .message("Event has ended. Thank you for participating!")
                .build();
    }
}
