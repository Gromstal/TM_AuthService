package org.example.taskmanager_authservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.AuthenticationRequest;
import org.example.taskmanager_authservice.dto.response.AuthenticationResponse;
import org.example.taskmanager_authservice.service.LoginService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse authenticationResponse = loginService.login(authenticationRequest);
        return ResponseEntity.ok(authenticationResponse);
    }
}
