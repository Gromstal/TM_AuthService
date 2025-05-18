package org.example.taskmanager_authservice.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerificationTokenRequest {
    private String email;
    private String token;
}
