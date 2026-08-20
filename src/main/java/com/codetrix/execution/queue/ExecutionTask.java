package com.codetrix.execution.queue;

import com.codetrix.execution.dto.ExecutionRequest;
import com.codetrix.execution.dto.ExecutionResult;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
public class ExecutionTask {

    private final String taskId;
    private final ExecutionRequest request;
    private final CompletableFuture<ExecutionResult> future;
    private final Instant createdAt;

    public ExecutionTask(ExecutionRequest request) {
        this.taskId = UUID.randomUUID().toString();
        this.request = request;
        this.future = new CompletableFuture<>();
        this.createdAt = Instant.now();
    }

    public void complete(ExecutionResult result) {
        future.complete(result);
    }

    public void fail(Throwable ex) {
        future.completeExceptionally(ex);
    }
}
