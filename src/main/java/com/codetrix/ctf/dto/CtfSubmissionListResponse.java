package com.codetrix.ctf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtfSubmissionListResponse {

    private List<CtfSubmissionResponse> submissions;
    private Integer totalPoints;
    private Integer challengesSolved;
    private Integer totalChallenges;
}
