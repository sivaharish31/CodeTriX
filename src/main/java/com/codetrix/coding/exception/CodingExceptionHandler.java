package com.codetrix.coding.exception;

import com.codetrix.auth.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@Order(1)
public class CodingExceptionHandler {

    @ExceptionHandler(CodingException.class)
    public ResponseEntity<ErrorResponse> handleCodingException(CodingException ex, HttpServletRequest request) {
        log.warn("Coding error: {} at {}", ex.getMessage(), request.getRequestURI());
        ErrorResponse response = ErrorResponse.of(
                ex.getStatus().value(),
                ex.getErrorCode(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(ex.getStatus()).body(response);
    }
}
