package com.codetrix.ctf.entity;

import com.codetrix.team.entity.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ctf_submissions", indexes = {
    @Index(name = "idx_ctf_sub_team", columnList = "team_id"),
    @Index(name = "idx_ctf_sub_challenge", columnList = "challenge_id"),
    @Index(name = "idx_ctf_sub_team_challenge", columnList = "team_id, challenge_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtfSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private CtfChallenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "submitted_flag", nullable = false)
    private String submittedFlag;

    @Column(nullable = false)
    @Builder.Default
    private Boolean correct = false;

    @Column(name = "points_awarded", nullable = false)
    @Builder.Default
    private Integer pointsAwarded = 0;

    @Column(name = "submission_time", nullable = false)
    private LocalDateTime submissionTime;

    @PrePersist
    protected void onCreate() {
        if (submissionTime == null) {
            submissionTime = LocalDateTime.now();
        }
    }
}
