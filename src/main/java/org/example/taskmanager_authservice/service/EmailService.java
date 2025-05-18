package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.client.EmailServiceClient;
import org.example.taskmanager_authservice.dto.request.VerificationTokenRequest;
import org.example.taskmanager_authservice.dto.response.RegistrationResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailServiceClient emailServiceClient;

    public RegistrationResponse sendVerificationEmail(String email, String token) {
        try {
            VerificationTokenRequest request = VerificationTokenRequest.builder()
                    .email(email)
                    .token(token)
                    .build();
          return  emailServiceClient.sendRegistrationEmail(request);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Email sending failed");
        }
    }
}
