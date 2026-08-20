package com.codetrix.coding.dto;

import com.codetrix.coding.entity.Language;
import com.codetrix.coding.entity.Submission;
import com.codetrix.coding.entity.SubmissionStatus;
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
public class SubmissionResponse {

    private Long id;
    private Long problemId;
    private String problemTitle;
    private Language language;
    private SubmissionStatus status;
    private Integer testsPassed;
    private Integer totalTests;
    private Integer pointsEarned;
    private Integer maxPoints;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
    private String compileOutput;
    private String errorMessage;
    private Instant submissionTime;

    public static SubmissionResponse fromEntity(Submission submission) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .problemId(submission.getProblemId())
                .language(submission.getLanguage())
                .status(submission.getStatus())
                .testsPassed(submission.getTestsPassed())
                .totalTests(submission.getTotalTests())
                .pointsEarned(submission.getPointsEarned())
                .executionTimeMs(submission.getExecutionTimeMs())
                .memoryUsedKb(submission.getMemoryUsedKb())
                .compileOutput(submission.getCompileOutput())
                .errorMessage(submission.getErrorMessage())
                .submissionTime(submission.getSubmissionTime())
                .build();
    }

    public static SubmissionResponse fromEntityWithProblem(Submission submission, String problemTitle, Integer maxPoints) {
        return SubmissionResponse.builder()
                .id(submission.getId())
                .problemId(submission.getProblemId())
                .problemTitle(problemTitle)
                .language(submission.getLanguage())
                .status(submission.getStatus())
                .testsPassed(submission.getTestsPassed())
                .totalTests(submission.getTotalTests())
                .pointsEarned(submission.getPointsEarned())
                .maxPoints(maxPoints)
                .executionTimeMs(submission.getExecutionTimeMs())
                .memoryUsedKb(submission.getMemoryUsedKb())
                .compileOutput(submission.getCompileOutput())
                .errorMessage(submission.getErrorMessage())
                .submissionTime(submission.getSubmissionTime())
                .build();
    }
}
