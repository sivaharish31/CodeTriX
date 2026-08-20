package com.codetrix.ctf.controller;

import com.codetrix.ctf.dto.*;
import com.codetrix.ctf.service.CtfChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/ctf")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class CtfAdminController {

    private final CtfChallengeService challengeService;

    @GetMapping("/challenges")
    public ResponseEntity<List<CtfChallengeAdminResponse>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallengesAdmin());
    }

    @GetMapping("/challenges/{id}")
    public ResponseEntity<CtfChallengeAdminResponse> getChallenge(@PathVariable Long id) {
        return ResponseEntity.ok(challengeService.getChallengeAdmin(id));
    }

    @PostMapping(value = "/challenges", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CtfChallengeAdminResponse> createChallenge(
            @Valid @RequestPart("challenge") CreateCtfChallengeRequest request,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) {
        CtfChallengeAdminResponse response = challengeService.createChallenge(request, attachment);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/challenges")
    public ResponseEntity<CtfChallengeAdminResponse> createChallengeJson(
            @Valid @RequestBody CreateCtfChallengeRequest request) {
        CtfChallengeAdminResponse response = challengeService.createChallenge(request, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/challenges/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CtfChallengeAdminResponse> updateChallenge(
            @PathVariable Long id,
            @Valid @RequestPart("challenge") UpdateCtfChallengeRequest request,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment) {
        return ResponseEntity.ok(challengeService.updateChallenge(id, request, attachment));
    }

    @PutMapping("/challenges/{id}")
    public ResponseEntity<CtfChallengeAdminResponse> updateChallengeJson(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCtfChallengeRequest request) {
        return ResponseEntity.ok(challengeService.updateChallenge(id, request, null));
    }

    @DeleteMapping("/challenges/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/challenges/{id}/attachment")
    public ResponseEntity<Map<String, String>> removeAttachment(@PathVariable Long id) {
        challengeService.removeAttachment(id);
        return ResponseEntity.ok(Map.of("message", "Attachment removed successfully"));
    }
}
