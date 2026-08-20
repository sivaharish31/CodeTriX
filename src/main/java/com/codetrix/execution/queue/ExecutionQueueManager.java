package com.codetrix.execution.queue;

import com.codetrix.execution.config.ExecutionConfig;
import com.codetrix.execution.dto.ExecutionRequest;
import com.codetrix.execution.dto.ExecutionResult;
import com.codetrix.execution.exception.ExecutionException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionQueueManager {

    private final ExecutionConfig config;

    private BlockingQueue<ExecutionTask> taskQueue;
    private ExecutorService executorService;
    private volatile boolean running = true;
    private final AtomicInteger activeExecutions = new AtomicInteger(0);
    private Function<ExecutionRequest, ExecutionResult> executor;

    @PostConstruct
    public void init() {
        taskQueue = new LinkedBlockingQueue<>(config.getQueueCapacity());

        executorService = Executors.newFixedThreadPool(
            config.getMaxConcurrentExecutions(),
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "execution-worker-" + counter.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                }
            }
        );

        for (int i = 0; i < config.getMaxConcurrentExecutions(); i++) {
            executorService.submit(this::processQueue);
        }

        log.info("Execution queue initialized with {} workers, capacity {}",
            config.getMaxConcurrentExecutions(), config.getQueueCapacity());
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Execution queue shutdown complete");
    }

    public void setExecutor(Function<ExecutionRequest, ExecutionResult> executor) {
        this.executor = executor;
    }

    public CompletableFuture<ExecutionResult> submit(ExecutionRequest request) {
        ExecutionTask task = new ExecutionTask(request);

        if (!taskQueue.offer(task)) {
            throw ExecutionException.queueFull();
        }

        log.debug("Task {} queued, queue size: {}", task.getTaskId(), taskQueue.size());
        return task.getFuture();
    }

    private void processQueue() {
        while (running) {
            try {
                ExecutionTask task = taskQueue.poll(1, TimeUnit.SECONDS);
                if (task == null) {
                    continue;
                }

                activeExecutions.incrementAndGet();
                try {
                    log.debug("Processing task {}", task.getTaskId());
                    ExecutionResult result = executor.apply(task.getRequest());
                    task.complete(result);
                    log.debug("Task {} completed with status {}", task.getTaskId(), result.getStatus());
                } catch (Exception e) {
                    log.error("Task {} failed: {}", task.getTaskId(), e.getMessage());
                    task.complete(ExecutionResult.internalError(e.getMessage()));
                } finally {
                    activeExecutions.decrementAndGet();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public int getQueueSize() {
        return taskQueue.size();
    }

    public int getActiveExecutions() {
        return activeExecutions.get();
    }

    public boolean isHealthy() {
        return running && !executorService.isShutdown();
    }
}
