package org.example.taskmanager_authservice.dto.response;

import lombok.Data;

@Data
public class AuthenticationResponse {

    private String accessToken;

    public AuthenticationResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
