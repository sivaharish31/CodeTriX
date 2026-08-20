package com.codetrix.ctf.dto;

import com.codetrix.ctf.entity.CtfCategory;
import com.codetrix.ctf.entity.CtfChallenge;
import com.codetrix.ctf.entity.CtfDifficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CtfChallengeResponse {

    private Long id;
    private String title;
    private String description;
    private CtfCategory category;
    private CtfDifficulty difficulty;
    private Integer points;
    private Boolean hasAttachment;
    private String attachmentFilename;
    private Boolean solved;

    public static CtfChallengeResponse from(CtfChallenge challenge) {
        return CtfChallengeResponse.builder()
            .id(challenge.getId())
            .title(challenge.getTitle())
            .description(challenge.getDescription())
            .category(challenge.getCategory())
            .difficulty(challenge.getDifficulty())
            .points(challenge.getPoints())
            .hasAttachment(challenge.hasAttachment())
            .attachmentFilename(challenge.getAttachmentFilename())
            .solved(false)
            .build();
    }

    public static CtfChallengeResponse from(CtfChallenge challenge, boolean solved) {
        CtfChallengeResponse response = from(challenge);
        response.setSolved(solved);
        return response;
    }
}
