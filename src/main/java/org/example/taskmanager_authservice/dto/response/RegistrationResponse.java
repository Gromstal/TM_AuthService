package org.example.taskmanager_authservice.dto.response;

import lombok.Data;

@Data
public class RegistrationResponse {

    private String message;

    public RegistrationResponse(String message) {
        this.message = message;
    }
}
