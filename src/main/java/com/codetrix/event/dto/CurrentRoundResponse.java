package com.codetrix.event.dto;

import com.codetrix.event.entity.Round;
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
public class CurrentRoundResponse {

    private Long roundId;
    private Integer roundNumber;
    private RoundType roundType;
    private RoundStatus status;
    private Instant startTime;
    private Instant endTime;
    private Instant serverTime;
    private Long remainingSeconds;
    private boolean acceptingSubmissions;

    public static CurrentRoundResponse fromRound(Round round, Instant serverTime) {
        long remaining = round.getRemainingSeconds(serverTime);
        boolean accepting = round.isRunning() && remaining > 0;

        return CurrentRoundResponse.builder()
                .roundId(round.getId())
                .roundNumber(round.getRoundNumber())
                .roundType(round.getRoundType())
                .status(round.getStatus())
                .startTime(round.getStartTime())
                .endTime(round.getEndTime())
                .serverTime(serverTime)
                .remainingSeconds(remaining)
                .acceptingSubmissions(accepting)
                .build();
    }

    public static CurrentRoundResponse noActiveRound(Instant serverTime) {
        return CurrentRoundResponse.builder()
                .serverTime(serverTime)
                .acceptingSubmissions(false)
                .build();
    }
}
