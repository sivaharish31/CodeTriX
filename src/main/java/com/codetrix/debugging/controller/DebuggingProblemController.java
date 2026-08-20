package com.codetrix.debugging.controller;

import com.codetrix.debugging.dto.*;
import com.codetrix.debugging.service.DebuggingProblemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DebuggingProblemController {

    private final DebuggingProblemService problemService;

    @GetMapping("/api/debugging/problems")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DebuggingProblemResponse>> getAllProblems() {
        List<DebuggingProblemResponse> problems = problemService.getAllProblemsForStudent();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/api/debugging/problems/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DebuggingProblemResponse> getProblem(@PathVariable Long id) {
        DebuggingProblemResponse problem = problemService.getProblemForStudent(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/api/admin/debugging/problems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DebuggingProblemAdminResponse>> getAllProblemsAdmin() {
        List<DebuggingProblemAdminResponse> problems = problemService.getAllProblemsForAdmin();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/api/admin/debugging/problems/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DebuggingProblemAdminResponse> getProblemAdmin(@PathVariable Long id) {
        DebuggingProblemAdminResponse problem = problemService.getProblemForAdmin(id);
        return ResponseEntity.ok(problem);
    }

    @PostMapping("/api/admin/debugging/problems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DebuggingProblemAdminResponse> createProblem(@Valid @RequestBody CreateDebuggingProblemRequest request) {
        log.info("Creating debugging problem: {}", request.getTitle());
        DebuggingProblemAdminResponse problem = problemService.createProblem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(problem);
    }

    @PutMapping("/api/admin/debugging/problems/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DebuggingProblemAdminResponse> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDebuggingProblemRequest request) {
        log.info("Updating debugging problem: {}", id);
        DebuggingProblemAdminResponse problem = problemService.updateProblem(id, request);
        return ResponseEntity.ok(problem);
    }

    @DeleteMapping("/api/admin/debugging/problems/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        log.info("Deleting debugging problem: {}", id);
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
