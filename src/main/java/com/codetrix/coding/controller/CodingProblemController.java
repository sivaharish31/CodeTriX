package com.codetrix.coding.controller;

import com.codetrix.coding.dto.*;
import com.codetrix.coding.service.CodingProblemService;
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
public class CodingProblemController {

    private final CodingProblemService problemService;

    @GetMapping("/api/coding/problems")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProblemResponse>> getAllProblems() {
        List<ProblemResponse> problems = problemService.getAllProblemsForStudent();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/api/coding/problems/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProblemResponse> getProblem(@PathVariable Long id) {
        ProblemResponse problem = problemService.getProblemForStudent(id);
        return ResponseEntity.ok(problem);
    }

    @GetMapping("/api/admin/coding/problems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProblemAdminResponse>> getAllProblemsAdmin() {
        List<ProblemAdminResponse> problems = problemService.getAllProblemsForAdmin();
        return ResponseEntity.ok(problems);
    }

    @GetMapping("/api/admin/coding/problems/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProblemAdminResponse> getProblemAdmin(@PathVariable Long id) {
        ProblemAdminResponse problem = problemService.getProblemForAdmin(id);
        return ResponseEntity.ok(problem);
    }

    @PostMapping("/api/admin/coding/problems")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProblemAdminResponse> createProblem(@Valid @RequestBody CreateProblemRequest request) {
        log.info("Creating problem: {}", request.getTitle());
        ProblemAdminResponse problem = problemService.createProblem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(problem);
    }

    @PutMapping("/api/admin/coding/problems/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProblemAdminResponse> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProblemRequest request) {
        log.info("Updating problem: {}", id);
        ProblemAdminResponse problem = problemService.updateProblem(id, request);
        return ResponseEntity.ok(problem);
    }

    @DeleteMapping("/api/admin/coding/problems/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id) {
        log.info("Deleting problem: {}", id);
        problemService.deleteProblem(id);
        return ResponseEntity.noContent().build();
    }
}
