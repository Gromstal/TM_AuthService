package org.example.taskmanager_authservice.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ForgotPasswordResponse {

    private String message;

    public ForgotPasswordResponse(String message) {
        this.message = message;
    }
}
