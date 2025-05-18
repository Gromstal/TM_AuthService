package org.example.taskmanager_authservice.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewPasswordRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;
}
