package org.example.taskmanager_authservice.dto.request;

import lombok.Data;

@Data
public class NewPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
