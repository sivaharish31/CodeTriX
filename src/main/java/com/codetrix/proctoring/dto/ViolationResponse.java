package com.codetrix.proctoring.dto;

import com.codetrix.proctoring.entity.Violation;
import com.codetrix.proctoring.entity.ViolationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationResponse {

    private Long id;
    private Long teamId;
    private String teamName;
    private Long roundId;
    private ViolationType violationType;
    private LocalDateTime violationTime;
    private String details;
    private LocalDateTime createdAt;

    public static ViolationResponse from(Violation violation) {
        return ViolationResponse.builder()
            .id(violation.getId())
            .teamId(violation.getTeam().getId())
            .teamName(violation.getTeam().getTeamName())
            .roundId(violation.getRoundId())
            .violationType(violation.getViolationType())
            .violationTime(violation.getViolationTime())
            .details(violation.getDetails())
            .createdAt(violation.getCreatedAt())
            .build();
    }
}
