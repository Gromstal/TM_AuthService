package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.TokenRequest;
import org.example.taskmanager_authservice.dto.response.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private static final Logger log = LoggerFactory.getLogger(RefreshService.class);

    public TokenResponse getNewAccessToken(TokenRequest refreshTokenRequest) {
        String token = refreshTokenRequest.getToken();
        log.info("Request to refresh access token with refresh token: {}", token);

        String username = tokenService.getSubjectFromToken(token);
        log.debug("Extracted username from token: {}", username);

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            log.debug("Loaded user details for username: {}", username);

            boolean isValid = tokenService.isRefreshTokenValid(token, userDetails);
            log.debug("Is refresh token valid: {}", isValid);

            if (!isValid) {
                log.warn("Invalid refresh token for user: {}", username);
                return new TokenResponse(null, "You need to login");
            }

            String newAccessToken = tokenService.generateAccessToken(userDetails);
            log.info("Generated new access token for user: {}", username);
            return new TokenResponse(newAccessToken, null);

        } catch (UsernameNotFoundException e) {
            log.error("Username not found during token refresh: {}", username);
            return new TokenResponse(null, "User not found");
        }
    }
}

