package com.codetrix.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "rounds")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "event")
@ToString(exclude = "event")
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "round_type", nullable = false, length = 20)
    private RoundType roundType;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "duration_seconds", nullable = false)
    @Builder.Default
    private Integer durationSeconds = 900; // 15 minutes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RoundStatus status = RoundStatus.LOCKED;

    public boolean isRunning() {
        return status == RoundStatus.RUNNING;
    }

    public boolean isCompleted() {
        return status == RoundStatus.COMPLETED;
    }

    public boolean isLocked() {
        return status == RoundStatus.LOCKED;
    }

    public long getRemainingSeconds(Instant now) {
        if (endTime == null || now.isAfter(endTime)) {
            return 0;
        }
        return endTime.getEpochSecond() - now.getEpochSecond();
    }
}
