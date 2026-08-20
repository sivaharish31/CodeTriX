package com.codetrix.ctf.controller;

import com.codetrix.ctf.dto.*;
import com.codetrix.ctf.entity.CtfChallenge;
import com.codetrix.ctf.exception.CtfException;
import com.codetrix.ctf.service.CtfAttachmentService;
import com.codetrix.ctf.service.CtfChallengeService;
import com.codetrix.ctf.service.CtfSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ctf")
@RequiredArgsConstructor
public class CtfChallengeController {

    private final CtfChallengeService challengeService;
    private final CtfSubmissionService submissionService;
    private final CtfAttachmentService attachmentService;

    @GetMapping("/challenges")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CtfChallengeResponse>> getChallenges() {
        Long teamId = getCurrentTeamId();
        return ResponseEntity.ok(challengeService.getActiveChallenges(teamId));
    }

    @GetMapping("/challenges/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CtfChallengeResponse> getChallenge(@PathVariable Long id) {
        Long teamId = getCurrentTeamId();
        return ResponseEntity.ok(challengeService.getChallenge(id, teamId));
    }

    @GetMapping("/challenges/{id}/attachment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
        CtfChallenge challenge = challengeService.getChallengeEntity(id);

        if (!challenge.hasAttachment()) {
            throw CtfException.attachmentNotFound(id);
        }

        Resource resource = attachmentService.loadAttachment(challenge.getAttachmentPath());

        String contentType = challenge.getAttachmentContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + challenge.getAttachmentFilename() + "\"")
            .body(resource);
    }

    @PostMapping("/challenges/{id}/submit")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<CtfSubmitResponse> submitFlag(
            @PathVariable Long id,
            @Valid @RequestBody CtfSubmitRequest request) {
        request.setChallengeId(id);
        Long teamId = getCurrentTeamId();
        return ResponseEntity.ok(submissionService.submitFlag(teamId, request));
    }

    @GetMapping("/submissions")
    @PreAuthorize("hasRole('TEAM')")
    public ResponseEntity<CtfSubmissionListResponse> getSubmissions() {
        Long teamId = getCurrentTeamId();
        return ResponseEntity.ok(submissionService.getSubmissions(teamId));
    }

    private Long getCurrentTeamId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.codetrix.auth.security.UserPrincipal principal) {
            return principal.getTeamId();
        }
        return null;
    }
}
