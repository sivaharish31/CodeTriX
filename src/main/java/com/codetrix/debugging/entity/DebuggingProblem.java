package com.codetrix.debugging.entity;

import com.codetrix.coding.entity.Language;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "debugging_problems")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DebuggingProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "buggy_code", columnDefinition = "TEXT", nullable = false)
    private String buggyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Language language;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "time_limit_ms", nullable = false)
    @Builder.Default
    private Integer timeLimitMs = 2000;

    @Column(name = "memory_limit_mb", nullable = false)
    @Builder.Default
    private Integer memoryLimitMb = 256;

    @Column(name = "hint", columnDefinition = "TEXT")
    private String hint;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    @Builder.Default
    private List<DebuggingTestCase> testCases = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void addTestCase(DebuggingTestCase testCase) {
        testCases.add(testCase);
        testCase.setProblem(this);
    }

    public void removeTestCase(DebuggingTestCase testCase) {
        testCases.remove(testCase);
        testCase.setProblem(null);
    }

    public List<DebuggingTestCase> getSampleTestCases() {
        return testCases.stream()
                .filter(DebuggingTestCase::getIsSample)
                .toList();
    }

    public List<DebuggingTestCase> getHiddenTestCases() {
        return testCases.stream()
                .filter(tc -> !tc.getIsSample())
                .toList();
    }
}
