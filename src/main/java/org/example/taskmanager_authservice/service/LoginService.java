package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.AuthenticationRequest;
import org.example.taskmanager_authservice.dto.response.AuthenticationResponse;
import org.example.taskmanager_authservice.entity.User;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public AuthenticationResponse login(AuthenticationRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUsername()));

        if (!user.isVerified()) {
            log.warn("Login attempt for unverified user: {}", request.getUsername());
            throw new AccessDeniedException("User is not verified");
        }

        log.info("Login attempt for user: {}", request.getUsername());

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            log.info("User {} authenticated successfully", request.getUsername());

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = tokenService.generateAccessToken(userDetails);
            String refreshToken = tokenService.generateRefreshToken(userDetails);
            log.info("Access and refresh tokens generated for user {}", request.getUsername());

            return new AuthenticationResponse(token, refreshToken);

        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            throw e;
        }
    }
}