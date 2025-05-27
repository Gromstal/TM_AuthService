package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.RegistrationRequest;
import org.example.taskmanager_authservice.dto.request.TokenRequest;
import org.example.taskmanager_authservice.dto.response.RegistrationResponse;
import org.example.taskmanager_authservice.entity.User;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);

    public RegistrationResponse startRegistration(RegistrationRequest request) {
        log.info("Start registration requested for email: {}", request.getEmail());

        return userRepository.findByEmail(request.getEmail())
                .map(user -> {
                    log.debug("User found for email {}: verified={}", request.getEmail(), user.isVerified());
                    if (user.isVerified()) {
                        log.info("User {} already registered and verified", request.getEmail());
                        return new RegistrationResponse("You are already registered");
                    } else {
                        String token = tokenService.saveVerificationMailToken(request.getEmail());
                        log.info("User {} not verified. Generated verification token: {}", request.getEmail(), token);
                        emailService.sendVerificationEmail(request.getEmail(), token);
                        log.info("Sent verification email to {}", request.getEmail());
                        return new RegistrationResponse("Check your email and try again.");
                    }
                })
                .orElseGet(() -> {
                    log.info("No user found for email {}. Creating new user.", request.getEmail());
                    User newUser = userService.createUser(request);
                    String verificationMailToken = tokenService.saveVerificationMailToken(request.getEmail());
                    RegistrationResponse response = emailService.sendVerificationEmail(request.getEmail(), verificationMailToken);
                    userRepository.save(newUser);
                    log.info("New user created and verification email sent to {}", request.getEmail());
                    log.debug("RegistrationResponse: {}", response);
                    return response;
                });
    }

    @Transactional
    public RegistrationResponse confirmRegistration(TokenRequest request) {
        log.info("Confirm registration called with token: {}", request.getToken());

        return tokenService.findVerificationMailToken(request.getToken())
                .filter(t -> !t.isUsed())
                .map(t -> {
                    log.debug("Token found for email {} with used status {}", t.getEmail(), t.isUsed());

                    if (tokenService.isMailTokenValid(request.getToken(), t.getEmail())) {
                        tokenService.setVerificationMailTokenIsUsed(t.getEmail());
                        userRepository.setIsVerified(t.getEmail());
                        log.info("Verification confirmed for email {}", t.getEmail());
                        return new RegistrationResponse("Verification is confirmed. Now you can login");
                    }
                    log.warn("Verification mail token expired for email {}", t.getEmail());
                    return new RegistrationResponse("Verification mail token expired");
                })
                .orElseGet(() -> {
                    log.warn("Verification mail token is empty or already used: {}", request.getToken());
                    return new RegistrationResponse("Verification mail token is empty or used");
                });
    }
}

