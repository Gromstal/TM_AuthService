package org.example.taskmanager_authservice.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ForgotPasswordRequest {
    private String username;
}
