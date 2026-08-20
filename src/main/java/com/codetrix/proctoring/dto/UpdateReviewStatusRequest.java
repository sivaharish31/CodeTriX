package com.codetrix.proctoring.dto;

import com.codetrix.proctoring.entity.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewStatusRequest {

    @NotNull(message = "Status is required")
    private ReviewStatus status;

    private String adminNotes;
}
