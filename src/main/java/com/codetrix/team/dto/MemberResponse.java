package com.codetrix.team.dto;

import com.codetrix.team.entity.TeamMember;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MemberResponse {

    private Long id;
    private String name;
    private String rollNumber;
    private String college;
    private String email;
    private LocalDateTime createdAt;

    public static MemberResponse fromEntity(TeamMember member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .rollNumber(member.getRollNumber())
                .college(member.getCollege())
                .email(member.getEmail())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
