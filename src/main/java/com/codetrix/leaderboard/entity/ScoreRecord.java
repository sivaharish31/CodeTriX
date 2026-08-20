package com.codetrix.leaderboard.entity;

import com.codetrix.team.entity.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "score_records", indexes = {
    @Index(name = "idx_score_records_team", columnList = "team_id"),
    @Index(name = "idx_score_records_type", columnList = "score_type"),
    @Index(name = "idx_score_records_unique", columnList = "team_id, score_type, problem_id", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "score_type", nullable = false)
    private ScoreType scoreType;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "submission_id")
    private Long submissionId;

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned;

    @Column(name = "max_points", nullable = false)
    private Integer maxPoints;

    @Column(name = "tests_passed")
    private Integer testsPassed;

    @Column(name = "total_tests")
    private Integer totalTests;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
