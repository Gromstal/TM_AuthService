package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.RegistrationRequest;
import org.example.taskmanager_authservice.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final BCryptPasswordEncoder passwordEncoder;

    public User createUser(RegistrationRequest registrationRequest) {
       return User.builder()
                .email(registrationRequest.getEmail())
                .role("User")
                .username(registrationRequest.getUsername())
                .password(getNewPassword(registrationRequest.getPassword()))
                .build();
    }

    public String getNewPassword(String password) {
        return passwordEncoder.encode(password);
    }
}
