package com.codetrix.ctf.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ctf_challenges")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtfChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CtfCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CtfDifficulty difficulty;

    @Column(nullable = false)
    private Integer points;

    @Column(nullable = false)
    private String flag;

    @Column(name = "attachment_filename")
    private String attachmentFilename;

    @Column(name = "attachment_path")
    private String attachmentPath;

    @Column(name = "attachment_content_type")
    private String attachmentContentType;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

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

    public boolean hasAttachment() {
        return attachmentPath != null && !attachmentPath.isEmpty();
    }
}
