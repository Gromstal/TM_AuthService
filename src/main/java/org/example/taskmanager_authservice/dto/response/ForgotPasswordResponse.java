package org.example.taskmanager_authservice.dto.response;

import lombok.Data;

@Data
public class ForgotPasswordResponse {

    private String message;

    public ForgotPasswordResponse(String message) {
        this.message = message;
    }
}
