package org.example.taskmanager_authservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.AuthenticationRequest;
import org.example.taskmanager_authservice.dto.request.TokenRequest;
import org.example.taskmanager_authservice.dto.response.AuthenticationResponse;
import org.example.taskmanager_authservice.dto.response.TokenResponse;
import org.example.taskmanager_authservice.service.LoginService;
import org.example.taskmanager_authservice.service.RefreshService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/refresh")
public class RefreshController {

    private final RefreshService refreshService;

    @PostMapping
    public ResponseEntity<TokenResponse> refresh(@RequestBody TokenRequest refreshTokenRequest) {
        TokenResponse freshAccessToken = refreshService.getNewAccessToken(refreshTokenRequest);
        return ResponseEntity.ok(freshAccessToken);
    }
}
