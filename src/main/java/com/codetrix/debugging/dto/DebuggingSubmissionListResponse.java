package com.codetrix.debugging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebuggingSubmissionListResponse {

    private List<DebuggingSubmissionResponse> submissions;
    private Integer totalSubmissions;
    private Integer totalPointsEarned;
    private Integer problemsSolved;
}
