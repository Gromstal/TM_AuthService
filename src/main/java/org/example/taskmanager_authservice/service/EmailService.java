package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.client.EmailServiceClient;
import org.example.taskmanager_authservice.dto.request.VerificationTokenRequest;
import org.example.taskmanager_authservice.dto.response.RegistrationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailServiceClient emailServiceClient;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public RegistrationResponse sendVerificationEmail(String email, String token) {
        log.info("Sending verification email to: {}", email);
        try {
            VerificationTokenRequest request = VerificationTokenRequest.builder()
                    .email(email)
                    .token(token)
                    .build();

            RegistrationResponse response = emailServiceClient.sendRegistrationEmail(request);

            log.info("Verification email sent to {} successfully", email);
            return response;

        } catch (Exception ex) {
            log.error("Failed to send verification email to {}: {}", email, ex.getMessage(), ex);
            throw new RuntimeException("Email sending failed", ex);
        }
    }
}

