package org.example.taskmanager_authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.RegistrationRequest;
import org.example.taskmanager_authservice.dto.response.RegistrationResponse;
import org.example.taskmanager_authservice.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        RegistrationResponse registrationResponse=registrationService.startRegistration(registrationRequest);
        return ResponseEntity.ok(registrationResponse);
    }

    @PostMapping("/confirm")
    public ResponseEntity<RegistrationResponse> confirm(@Valid @RequestBody String token) {
        RegistrationResponse registrationResponse = registrationService.confirmRegistration(token);
        return ResponseEntity.ok(registrationResponse);
    }
}
