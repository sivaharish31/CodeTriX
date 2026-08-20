package com.codetrix.auth.service;

import com.codetrix.auth.dto.*;
import com.codetrix.auth.entity.User;
import com.codetrix.auth.exception.AuthException;
import com.codetrix.auth.repository.UserRepository;
import com.codetrix.common.enums.RoleType;
import com.codetrix.team.entity.Team;
import com.codetrix.team.entity.TeamStatus;
import com.codetrix.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    @Transactional(readOnly = true)
    public AuthResponse authenticateAdmin(AdminLoginRequest request) {
        log.debug("Authenticating admin: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(AuthException::invalidCredentials);

        if (!user.getEnabled()) {
            throw AuthException.accountDisabled();
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for admin: {}", request.getUsername());
            throw AuthException.invalidCredentials();
        }

        if (user.getRole().getName() != RoleType.ADMIN) {
            log.warn("Non-admin user attempted admin login: {}", request.getUsername());
            throw AuthException.accessDenied();
        }

        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("displayName", user.getDisplayName());
        if (user.getEmail() != null) {
            additionalClaims.put("email", user.getEmail());
        }

        String token = jwtService.generateToken(
                user.getUsername(),
                user.getRole().getName().name(),
                "ADMIN",
                additionalClaims
        );

        log.info("Admin authenticated successfully: {}", request.getUsername());

        return AuthResponse.of(
                token,
                jwtService.getExpirationTime() / 1000,
                user.getRole().getName().name(),
                user.getUsername(),
                user.getDisplayName()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse authenticateTeam(TeamLoginRequest request) {
        log.debug("Authenticating team: {}", request.getTeamId());

        Team team = teamRepository.findByTeamCode(request.getTeamId())
                .orElseThrow(AuthException::invalidCredentials);

        if (team.getStatus() == TeamStatus.DISQUALIFIED) {
            throw AuthException.accountDisabled();
        }

        if (!passwordEncoder.matches(request.getLoginPin(), team.getLoginPinHash())) {
            log.warn("Invalid PIN attempt for team: {}", request.getTeamId());
            throw AuthException.invalidCredentials();
        }

        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("teamName", team.getTeamName());
        additionalClaims.put("teamId", team.getId());

        String token = jwtService.generateToken(
                team.getTeamCode(),
                "TEAM",
                "TEAM",
                additionalClaims
        );

        log.info("Team authenticated successfully: {}", request.getTeamId());

        return AuthResponse.of(
                token,
                jwtService.getExpirationTime() / 1000,
                "TEAM",
                team.getTeamCode(),
                team.getTeamName()
        );
    }

    public MessageResponse logout(String token) {
        try {
            String tokenId = jwtService.extractTokenId(token);
            Date expiration = jwtService.extractExpiration(token);

            tokenBlacklistService.blacklistToken(tokenId, expiration);

            log.info("Token successfully invalidated");
            return MessageResponse.success("Successfully logged out");
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            throw AuthException.invalidToken();
        }
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getCurrentUser(String token) {
        try {
            String subject = jwtService.extractSubject(token);
            String userType = jwtService.extractUserType(token);
            String role = jwtService.extractRole(token);

            if ("ADMIN".equals(userType)) {
                User user = userRepository.findByUsername(subject)
                        .orElseThrow(AuthException::userNotFound);

                return UserInfoResponse.builder()
                        .identifier(user.getUsername())
                        .displayName(user.getDisplayName())
                        .role(role)
                        .email(user.getEmail())
                        .userType(userType)
                        .build();
            } else {
                Team team = teamRepository.findByTeamCode(subject)
                        .orElseThrow(AuthException::teamNotFound);

                return UserInfoResponse.builder()
                        .identifier(team.getTeamCode())
                        .displayName(team.getTeamName())
                        .role(role)
                        .userType(userType)
                        .build();
            }
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            throw AuthException.invalidToken();
        }
    }
}
