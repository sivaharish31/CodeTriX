package com.codetrix.proctoring.dto;

import com.codetrix.proctoring.entity.ViolationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationRequest {

    @NotNull(message = "Violation type is required")
    private ViolationType violationType;

    private Long roundId;

    private Long clientTimestamp;

    private String details;
}
