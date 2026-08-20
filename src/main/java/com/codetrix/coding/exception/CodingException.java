package com.codetrix.coding.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CodingException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public CodingException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "CODING_ERROR";
    }

    public CodingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "CODING_ERROR";
    }

    public CodingException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static CodingException problemNotFound(Long id) {
        return new CodingException(
            "Problem not found with id: " + id,
            HttpStatus.NOT_FOUND,
            "PROBLEM_NOT_FOUND"
        );
    }

    public static CodingException submissionNotFound(Long id) {
        return new CodingException(
            "Submission not found with id: " + id,
            HttpStatus.NOT_FOUND,
            "SUBMISSION_NOT_FOUND"
        );
    }

    public static CodingException duplicateTitle(String title) {
        return new CodingException(
            "Problem with title already exists: " + title,
            HttpStatus.CONFLICT,
            "DUPLICATE_TITLE"
        );
    }

    public static CodingException roundNotActive() {
        return new CodingException(
            "Coding round is not currently active",
            HttpStatus.FORBIDDEN,
            "ROUND_NOT_ACTIVE"
        );
    }

    public static CodingException submissionAfterDeadline() {
        return new CodingException(
            "Submission rejected: coding round has ended",
            HttpStatus.FORBIDDEN,
            "SUBMISSION_AFTER_DEADLINE"
        );
    }

    public static CodingException eventNotStarted() {
        return new CodingException(
            "Event has not started yet",
            HttpStatus.FORBIDDEN,
            "EVENT_NOT_STARTED"
        );
    }

    public static CodingException cannotModifyAfterStart() {
        return new CodingException(
            "Cannot modify problems after event has started",
            HttpStatus.FORBIDDEN,
            "CANNOT_MODIFY_AFTER_START"
        );
    }

    public static CodingException unsupportedLanguage(String language) {
        return new CodingException(
            "Unsupported language: " + language,
            HttpStatus.BAD_REQUEST,
            "UNSUPPORTED_LANGUAGE"
        );
    }
}
