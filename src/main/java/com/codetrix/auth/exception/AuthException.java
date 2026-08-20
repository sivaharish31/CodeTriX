package com.codetrix.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public AuthException(String message) {
        super(message);
        this.status = HttpStatus.UNAUTHORIZED;
        this.errorCode = "AUTH_ERROR";
    }

    public AuthException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = "AUTH_ERROR";
    }

    public AuthException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }

    public static AuthException invalidCredentials() {
        return new AuthException("Invalid credentials", HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS");
    }

    public static AuthException accountDisabled() {
        return new AuthException("Account is disabled", HttpStatus.FORBIDDEN, "ACCOUNT_DISABLED");
    }

    public static AuthException tokenExpired() {
        return new AuthException("Token has expired", HttpStatus.UNAUTHORIZED, "TOKEN_EXPIRED");
    }

    public static AuthException invalidToken() {
        return new AuthException("Invalid token", HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
    }

    public static AuthException accessDenied() {
        return new AuthException("Access denied", HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }

    public static AuthException userNotFound() {
        return new AuthException("User not found", HttpStatus.NOT_FOUND, "USER_NOT_FOUND");
    }

    public static AuthException teamNotFound() {
        return new AuthException("Team not found", HttpStatus.NOT_FOUND, "TEAM_NOT_FOUND");
    }
}
