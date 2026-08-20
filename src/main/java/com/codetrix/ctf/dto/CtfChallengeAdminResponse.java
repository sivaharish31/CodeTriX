package com.codetrix.ctf.dto;

import com.codetrix.ctf.entity.CtfCategory;
import com.codetrix.ctf.entity.CtfChallenge;
import com.codetrix.ctf.entity.CtfDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtfChallengeAdminResponse {

    private Long id;
    private String title;
    private String description;
    private CtfCategory category;
    private CtfDifficulty difficulty;
    private Integer points;
    private String flag;
    private Boolean active;
    private Boolean hasAttachment;
    private String attachmentFilename;
    private String attachmentContentType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CtfChallengeAdminResponse from(CtfChallenge challenge) {
        return CtfChallengeAdminResponse.builder()
            .id(challenge.getId())
            .title(challenge.getTitle())
            .description(challenge.getDescription())
            .category(challenge.getCategory())
            .difficulty(challenge.getDifficulty())
            .points(challenge.getPoints())
            .flag(challenge.getFlag())
            .active(challenge.getActive())
            .hasAttachment(challenge.hasAttachment())
            .attachmentFilename(challenge.getAttachmentFilename())
            .attachmentContentType(challenge.getAttachmentContentType())
            .createdAt(challenge.getCreatedAt())
            .updatedAt(challenge.getUpdatedAt())
            .build();
    }
}
