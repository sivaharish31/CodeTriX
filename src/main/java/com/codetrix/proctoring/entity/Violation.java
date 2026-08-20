package com.codetrix.proctoring.entity;

import com.codetrix.team.entity.Team;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "proctoring_violations", indexes = {
    @Index(name = "idx_violations_team", columnList = "team_id"),
    @Index(name = "idx_violations_round", columnList = "round_id"),
    @Index(name = "idx_violations_type", columnList = "violation_type"),
    @Index(name = "idx_violations_time", columnList = "violation_time")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "round_id", nullable = false)
    private Long roundId;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_type", nullable = false)
    private ViolationType violationType;

    @Column(name = "violation_time", nullable = false)
    private LocalDateTime violationTime;

    @Column(name = "client_timestamp")
    private Long clientTimestamp;

    @Column(name = "details")
    private String details;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (violationTime == null) {
            violationTime = LocalDateTime.now();
        }
    }
}
