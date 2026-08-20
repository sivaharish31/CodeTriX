package com.codetrix.proctoring.dto;

import com.codetrix.proctoring.entity.ReviewStatus;
import com.codetrix.proctoring.entity.ViolationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamViolationSummary {

    private Long teamId;
    private String teamName;
    private Integer totalViolations;
    private Map<ViolationType, Integer> violationsByType;
    private List<ViolationResponse> recentViolations;
    private ReviewStatus reviewStatus;
    private String adminNotes;
    private LocalDateTime lastViolationTime;
}
