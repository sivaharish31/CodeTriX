package com.codetrix.event.dto;

import com.codetrix.event.entity.EventStatus;
import com.codetrix.event.entity.RoundStatus;
import com.codetrix.event.entity.RoundType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeResponse {

    private Instant serverTime;
    private EventStatus eventStatus;
    private Long eventRemainingSeconds;
    private Integer currentRoundNumber;
    private RoundType currentRoundType;
    private RoundStatus currentRoundStatus;
    private Long roundRemainingSeconds;
    private Instant roundEndTime;

    public static TimeResponse notStarted(Instant serverTime) {
        return TimeResponse.builder()
                .serverTime(serverTime)
                .eventStatus(EventStatus.NOT_STARTED)
                .build();
    }

    public static TimeResponse completed(Instant serverTime) {
        return TimeResponse.builder()
                .serverTime(serverTime)
                .eventStatus(EventStatus.COMPLETED)
                .eventRemainingSeconds(0L)
                .roundRemainingSeconds(0L)
                .build();
    }
}
