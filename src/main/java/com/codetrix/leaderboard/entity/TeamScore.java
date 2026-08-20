package com.codetrix.leaderboard.entity;

import com.codetrix.team.entity.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_scores", indexes = {
    @Index(name = "idx_team_scores_total", columnList = "total_score DESC"),
    @Index(name = "idx_team_scores_team", columnList = "team_id", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false, unique = true)
    private Team team;

    @Column(name = "coding_score", nullable = false)
    @Builder.Default
    private Integer codingScore = 0;

    @Column(name = "debugging_score", nullable = false)
    @Builder.Default
    private Integer debuggingScore = 0;

    @Column(name = "ctf_score", nullable = false)
    @Builder.Default
    private Integer ctfScore = 0;

    @Column(name = "total_score", nullable = false)
    @Builder.Default
    private Integer totalScore = 0;

    @Column(name = "coding_problems_solved", nullable = false)
    @Builder.Default
    private Integer codingProblemsSolved = 0;

    @Column(name = "debugging_problems_solved", nullable = false)
    @Builder.Default
    private Integer debuggingProblemsSolved = 0;

    @Column(name = "ctf_challenges_solved", nullable = false)
    @Builder.Default
    private Integer ctfChallengesSolved = 0;

    @Column(name = "last_submission_time")
    private LocalDateTime lastSubmissionTime;

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
        totalScore = codingScore + debuggingScore + ctfScore;
    }

    public void recalculateTotal() {
        this.totalScore = codingScore + debuggingScore + ctfScore;
    }
}
