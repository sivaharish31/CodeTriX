package com.codetrix.proctoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminViolationsResponse {

    private List<ViolationResponse> violations;
    private Integer totalViolations;
    private Integer teamsWithViolations;
    private Map<String, Integer> violationsByType;
    private List<TeamViolationCount> topOffenders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamViolationCount {
        private Long teamId;
        private String teamName;
        private Integer count;
    }
}
