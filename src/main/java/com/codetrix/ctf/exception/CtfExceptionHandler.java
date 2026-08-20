package com.codetrix.ctf.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.codetrix.ctf")
@Order(1)
public class CtfExceptionHandler {

    @ExceptionHandler(CtfException.class)
    public ResponseEntity<Map<String, Object>> handleCtfException(CtfException ex) {
        log.warn("CTF exception: {} - {}", ex.getCode(), ex.getMessage());

        Map<String, Object> body = Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status", ex.getStatus().value(),
            "error", ex.getStatus().getReasonPhrase(),
            "code", ex.getCode(),
            "message", ex.getMessage()
        );

        return ResponseEntity.status(ex.getStatus()).body(body);
    }
}
