package org.example.taskmanager_authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationRequest {
    private String username;
    private String password;

    @NotBlank
    @Email
    private String email;
}
