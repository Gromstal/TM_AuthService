package org.example.taskmanager_authservice.service;

import org.example.taskmanager_authservice.dto.request.RegistrationRequest;
import org.example.taskmanager_authservice.entity.User;

public interface UserService {
    User createUser (RegistrationRequest registrationRequest);
    String getNewPassword(String password);
}
