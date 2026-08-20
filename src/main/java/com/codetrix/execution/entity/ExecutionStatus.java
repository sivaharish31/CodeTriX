package com.codetrix.execution.entity;

public enum ExecutionStatus {
    QUEUED,
    COMPILING,
    RUNNING,
    ACCEPTED,
    WRONG_ANSWER,
    PARTIAL,
    COMPILATION_ERROR,
    RUNTIME_ERROR,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    INTERNAL_ERROR
}
