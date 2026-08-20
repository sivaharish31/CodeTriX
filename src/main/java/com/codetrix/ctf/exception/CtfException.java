package com.codetrix.ctf.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CtfException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public CtfException(String message, HttpStatus status, String code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public static CtfException challengeNotFound(Long id) {
        return new CtfException(
            "CTF challenge not found with id: " + id,
            HttpStatus.NOT_FOUND,
            "CTF_CHALLENGE_NOT_FOUND"
        );
    }

    public static CtfException challengeNotActive(Long id) {
        return new CtfException(
            "CTF challenge is not active: " + id,
            HttpStatus.BAD_REQUEST,
            "CTF_CHALLENGE_NOT_ACTIVE"
        );
    }

    public static CtfException duplicateTitle(String title) {
        return new CtfException(
            "A challenge with this title already exists: " + title,
            HttpStatus.CONFLICT,
            "CTF_DUPLICATE_TITLE"
        );
    }

    public static CtfException roundNotActive() {
        return new CtfException(
            "CTF round is not currently active. Submissions are only accepted during Round 3.",
            HttpStatus.FORBIDDEN,
            "CTF_ROUND_NOT_ACTIVE"
        );
    }

    public static CtfException rateLimitExceeded() {
        return new CtfException(
            "Too many flag attempts. Please wait before trying again.",
            HttpStatus.TOO_MANY_REQUESTS,
            "CTF_RATE_LIMIT_EXCEEDED"
        );
    }

    public static CtfException attachmentNotFound(Long challengeId) {
        return new CtfException(
            "No attachment found for challenge: " + challengeId,
            HttpStatus.NOT_FOUND,
            "CTF_ATTACHMENT_NOT_FOUND"
        );
    }

    public static CtfException attachmentUploadFailed(String reason) {
        return new CtfException(
            "Failed to upload attachment: " + reason,
            HttpStatus.INTERNAL_SERVER_ERROR,
            "CTF_ATTACHMENT_UPLOAD_FAILED"
        );
    }

    public static CtfException invalidAttachmentType(String contentType) {
        return new CtfException(
            "Invalid attachment type: " + contentType + ". Executable files are not allowed.",
            HttpStatus.BAD_REQUEST,
            "CTF_INVALID_ATTACHMENT_TYPE"
        );
    }
}
