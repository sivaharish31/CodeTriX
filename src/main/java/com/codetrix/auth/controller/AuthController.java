package com.codetrix.auth.controller;

import com.codetrix.auth.dto.*;
import com.codetrix.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody AdminLoginRequest request) {
        log.info("Admin login attempt for: {}", request.getUsername());
        AuthResponse response = authService.authenticateAdmin(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/team/login")
    public ResponseEntity<AuthResponse> teamLogin(@Valid @RequestBody TeamLoginRequest request) {
        log.info("Team login attempt for: {}", request.getTeamId());
        AuthResponse response = authService.authenticateTeam(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(MessageResponse.error("Invalid authorization header"));
        }
        MessageResponse response = authService.logout(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        UserInfoResponse response = authService.getCurrentUser(token);
        return ResponseEntity.ok(response);
    }

    private String extractTokenFromHeader(String authHeader) {
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
