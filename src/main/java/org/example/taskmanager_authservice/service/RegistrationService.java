package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.RegistrationRequest;
import org.example.taskmanager_authservice.dto.response.RegistrationResponse;
import org.example.taskmanager_authservice.entity.User;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final UserService userService;

    public RegistrationResponse startRegistration(RegistrationRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .map(t -> {
                    return new RegistrationResponse("Email is already in use");
                })
                .orElseGet(() -> {
                    User newUser = userService.createUser(request);
                    String verificationMailToken = tokenService.saveVerificationMailToken(request.getEmail());
                    RegistrationResponse response= emailService.sendVerificationEmail(request.getEmail(), verificationMailToken);
                    userRepository.save(newUser);
                    return response;
                });
    }

    @Transactional
    public RegistrationResponse confirmRegistration(String token) {
        return tokenService.findVerificationMailToken(token)
                .filter(t -> !t.isUsed())
                .map(t -> {

                    if (tokenService.isMailTokenValid(token, t.getEmail())) {
                        tokenService.setVerificationMailTokenIsUsed(t.getEmail());
                        userRepository.setIsVerified(t.getEmail());
                        return new RegistrationResponse("Verification is confirmed. Now you can login");
                    }
                    return new RegistrationResponse("Verification mail token expired");
                })
                .orElse(new RegistrationResponse("Verification mail token is empty or used"));
    }
}
