package com.codetrix.execution.controller;

import com.codetrix.execution.dto.ExecutionRequest;
import com.codetrix.execution.dto.ExecutionResult;
import com.codetrix.execution.service.DockerExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/execution")
@RequiredArgsConstructor
public class ExecutionController {

    private final DockerExecutionService executionService;

    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExecutionResult> execute(@Valid @RequestBody ExecutionRequest request) {
        ExecutionResult result = executionService.executeSync(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DockerExecutionService.ExecutionQueueStatus> getStatus() {
        return ResponseEntity.ok(executionService.getQueueStatus());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean dockerAvailable = executionService.isDockerAvailable();
        DockerExecutionService.ExecutionQueueStatus queueStatus = executionService.getQueueStatus();

        Map<String, Object> health = Map.of(
            "status", dockerAvailable && queueStatus.isHealthy() ? "UP" : "DOWN",
            "dockerAvailable", dockerAvailable,
            "queueHealthy", queueStatus.isHealthy(),
            "queueSize", queueStatus.getQueueSize(),
            "activeExecutions", queueStatus.getActiveExecutions()
        );

        return ResponseEntity.ok(health);
    }
}
