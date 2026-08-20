package com.codetrix.execution.exception;

import lombok.Getter;

@Getter
public class ExecutionException extends RuntimeException {

    private final String code;

    public ExecutionException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ExecutionException(String message, String code, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public static ExecutionException dockerNotAvailable() {
        return new ExecutionException(
            "Docker is not available or not running",
            "DOCKER_UNAVAILABLE"
        );
    }

    public static ExecutionException containerCreationFailed(String reason) {
        return new ExecutionException(
            "Failed to create execution container: " + reason,
            "CONTAINER_CREATION_FAILED"
        );
    }

    public static ExecutionException executionTimeout() {
        return new ExecutionException(
            "Code execution timed out",
            "EXECUTION_TIMEOUT"
        );
    }

    public static ExecutionException queueFull() {
        return new ExecutionException(
            "Execution queue is full. Please try again later.",
            "QUEUE_FULL"
        );
    }

    public static ExecutionException unsupportedLanguage(String language) {
        return new ExecutionException(
            "Unsupported language: " + language,
            "UNSUPPORTED_LANGUAGE"
        );
    }
}
