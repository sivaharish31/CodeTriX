package com.codetrix.debugging.entity;

import com.codetrix.coding.entity.Language;
import com.codetrix.coding.entity.SubmissionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "debugging_submissions", indexes = {
    @Index(name = "idx_debug_submission_team", columnList = "team_id"),
    @Index(name = "idx_debug_submission_problem", columnList = "problem_id"),
    @Index(name = "idx_debug_submission_time", columnList = "submission_time")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebuggingSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "team_code", nullable = false, length = 20)
    private String teamCode;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Language language;

    @Column(name = "source_code", columnDefinition = "TEXT", nullable = false)
    private String sourceCode;

    @Column(name = "submission_time", nullable = false)
    private Instant submissionTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.QUEUED;

    @Column(name = "tests_passed")
    @Builder.Default
    private Integer testsPassed = 0;

    @Column(name = "total_tests")
    @Builder.Default
    private Integer totalTests = 0;

    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "memory_used_kb")
    private Integer memoryUsedKb;

    @Column(name = "compile_output", columnDefinition = "TEXT")
    private String compileOutput;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    public boolean isAccepted() {
        return status == SubmissionStatus.ACCEPTED;
    }

    public boolean isPartial() {
        return status == SubmissionStatus.PARTIAL;
    }

    public boolean isPending() {
        return status == SubmissionStatus.QUEUED || status == SubmissionStatus.RUNNING;
    }
}
