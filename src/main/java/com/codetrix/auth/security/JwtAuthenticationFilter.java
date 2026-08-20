package com.codetrix.auth.security;

import com.codetrix.auth.service.CustomUserDetailsService;
import com.codetrix.auth.service.JwtService;
import com.codetrix.auth.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);

            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                processJwtAuthentication(jwt, request);
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            request.setAttribute("jwt_error", "Token has expired");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            request.setAttribute("jwt_error", "Invalid token format");
        } catch (SecurityException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            request.setAttribute("jwt_error", "Invalid token signature");
        } catch (Exception e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
            request.setAttribute("jwt_error", "Authentication failed");
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private void processJwtAuthentication(String jwt, HttpServletRequest request) {
        if (!jwtService.validateToken(jwt)) {
            log.warn("Invalid JWT token");
            return;
        }

        String tokenId = jwtService.extractTokenId(jwt);
        if (tokenBlacklistService.isBlacklisted(tokenId)) {
            log.warn("Attempted use of blacklisted token");
            request.setAttribute("jwt_error", "Token has been revoked");
            return;
        }

        String subject = jwtService.extractSubject(jwt);
        String role = jwtService.extractRole(jwt);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                subject,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("Set authentication for user: {} with role: {}", subject, role);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/admin/login") ||
               path.startsWith("/api/auth/team/login") ||
               path.startsWith("/h2-console") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs");
    }
}
