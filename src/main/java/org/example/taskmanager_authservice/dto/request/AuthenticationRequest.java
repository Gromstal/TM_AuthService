package org.example.taskmanager_authservice.dto.request;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String username;
    private String password;
}
