package com.codetrix.ctf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtfSubmitResponse {

    private Long submissionId;
    private Long challengeId;
    private String challengeTitle;
    private Boolean correct;
    private Integer pointsAwarded;
    private String message;

    public static CtfSubmitResponse correct(Long submissionId, Long challengeId, String title, Integer points) {
        return CtfSubmitResponse.builder()
            .submissionId(submissionId)
            .challengeId(challengeId)
            .challengeTitle(title)
            .correct(true)
            .pointsAwarded(points)
            .message("Correct flag! +" + points + " points")
            .build();
    }

    public static CtfSubmitResponse incorrect(Long submissionId, Long challengeId, String title) {
        return CtfSubmitResponse.builder()
            .submissionId(submissionId)
            .challengeId(challengeId)
            .challengeTitle(title)
            .correct(false)
            .pointsAwarded(0)
            .message("Incorrect flag. Try again!")
            .build();
    }

    public static CtfSubmitResponse alreadySolved(Long challengeId, String title, Integer points) {
        return CtfSubmitResponse.builder()
            .challengeId(challengeId)
            .challengeTitle(title)
            .correct(true)
            .pointsAwarded(0)
            .message("You have already solved this challenge. No additional points awarded.")
            .build();
    }
}
