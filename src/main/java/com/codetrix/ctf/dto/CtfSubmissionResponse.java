package com.codetrix.ctf.dto;

import com.codetrix.ctf.entity.CtfCategory;
import com.codetrix.ctf.entity.CtfSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtfSubmissionResponse {

    private Long id;
    private Long challengeId;
    private String challengeTitle;
    private CtfCategory challengeCategory;
    private Integer challengePoints;
    private Boolean correct;
    private Integer pointsAwarded;
    private LocalDateTime submissionTime;

    public static CtfSubmissionResponse from(CtfSubmission submission) {
        return CtfSubmissionResponse.builder()
            .id(submission.getId())
            .challengeId(submission.getChallenge().getId())
            .challengeTitle(submission.getChallenge().getTitle())
            .challengeCategory(submission.getChallenge().getCategory())
            .challengePoints(submission.getChallenge().getPoints())
            .correct(submission.getCorrect())
            .pointsAwarded(submission.getPointsAwarded())
            .submissionTime(submission.getSubmissionTime())
            .build();
    }
}
