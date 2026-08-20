package com.codetrix.event.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EventException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public EventException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
        this.errorCode = "EVENT_ERROR";
    }

    public EventException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "EVENT_ERROR";
    }

    public EventException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static EventException eventNotFound() {
        return new EventException(
            "No event found",
            HttpStatus.NOT_FOUND,
            "EVENT_NOT_FOUND"
        );
    }

    public static EventException eventAlreadyStarted() {
        return new EventException(
            "Event has already started and cannot be modified",
            HttpStatus.CONFLICT,
            "EVENT_ALREADY_STARTED"
        );
    }

    public static EventException eventNotStarted() {
        return new EventException(
            "Event has not started yet",
            HttpStatus.BAD_REQUEST,
            "EVENT_NOT_STARTED"
        );
    }

    public static EventException eventCompleted() {
        return new EventException(
            "Event has already completed",
            HttpStatus.GONE,
            "EVENT_COMPLETED"
        );
    }

    public static EventException submissionAfterDeadline() {
        return new EventException(
            "Submission rejected: deadline has passed",
            HttpStatus.FORBIDDEN,
            "SUBMISSION_AFTER_DEADLINE"
        );
    }

    public static EventException roundNotActive() {
        return new EventException(
            "No active round at this time",
            HttpStatus.BAD_REQUEST,
            "ROUND_NOT_ACTIVE"
        );
    }

    public static EventException invalidOperation(String message) {
        return new EventException(
            message,
            HttpStatus.BAD_REQUEST,
            "INVALID_OPERATION"
        );
    }

    public static EventException timerModificationNotAllowed() {
        return new EventException(
            "Timer modification is not allowed after event starts",
            HttpStatus.FORBIDDEN,
            "TIMER_MODIFICATION_FORBIDDEN"
        );
    }
}
