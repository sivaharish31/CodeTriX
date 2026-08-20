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
public class RoundStatusResponse {

    private Long roundId;
    private Integer roundNumber;
    private RoundType roundType;
    private RoundStatus status;
    private Instant startTime;
    private Instant endTime;
    private Integer durationSeconds;
    private Long remainingSeconds;

    public static RoundStatusResponse fromEntity(Round round, Instant serverTime) {
        Long remaining = null;
        if (round.isRunning() && round.getEndTime() != null) {
            remaining = Math.max(0, round.getEndTime().getEpochSecond() - serverTime.getEpochSecond());
        }

        return RoundStatusResponse.builder()
                .roundId(round.getId())
                .roundNumber(round.getRoundNumber())
                .roundType(round.getRoundType())
                .status(round.getStatus())
                .startTime(round.getStartTime())
                .endTime(round.getEndTime())
                .durationSeconds(round.getDurationSeconds())
                .remainingSeconds(remaining)
                .build();
    }
}
