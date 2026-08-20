package com.codetrix.debugging.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DebuggingException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public DebuggingException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "DEBUGGING_ERROR";
    }

    public DebuggingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "DEBUGGING_ERROR";
    }

    public DebuggingException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static DebuggingException problemNotFound(Long id) {
        return new DebuggingException(
            "Debugging problem not found with id: " + id,
            HttpStatus.NOT_FOUND,
            "PROBLEM_NOT_FOUND"
        );
    }

    public static DebuggingException submissionNotFound(Long id) {
        return new DebuggingException(
            "Submission not found with id: " + id,
            HttpStatus.NOT_FOUND,
            "SUBMISSION_NOT_FOUND"
        );
    }

    public static DebuggingException duplicateTitle(String title) {
        return new DebuggingException(
            "Problem with title already exists: " + title,
            HttpStatus.CONFLICT,
            "DUPLICATE_TITLE"
        );
    }

    public static DebuggingException roundNotActive() {
        return new DebuggingException(
            "Debugging round is not currently active",
            HttpStatus.FORBIDDEN,
            "ROUND_NOT_ACTIVE"
        );
    }

    public static DebuggingException submissionAfterDeadline() {
        return new DebuggingException(
            "Submission rejected: debugging round has ended",
            HttpStatus.FORBIDDEN,
            "SUBMISSION_AFTER_DEADLINE"
        );
    }

    public static DebuggingException cannotModifyAfterStart() {
        return new DebuggingException(
            "Cannot modify problems after event has started",
            HttpStatus.FORBIDDEN,
            "CANNOT_MODIFY_AFTER_START"
        );
    }
}
