package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.client.EmailServiceClient;
import org.example.taskmanager_authservice.dto.request.VerificationTokenRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailServiceClient emailServiceClient;

    public void sendVerificationEmail(String email, String token) {
        try {
            VerificationTokenRequest request = VerificationTokenRequest.builder()
                    .email(email)
                    .token(token)
                    .build();
            emailServiceClient.sendRegistrationEmail(request);
        } catch (Exception ex) {
            //TODO добавить логирование
            throw new RuntimeException("Email sending failed");
        }
    }
}
