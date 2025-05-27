package org.example.taskmanager_authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.ChangePasswordRequest;
import org.example.taskmanager_authservice.dto.response.ChangePasswordResponse;
import org.example.taskmanager_authservice.service.ChangePasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/change")
public class ChangePasswordController {

    private final ChangePasswordService changePasswordService;

    @PostMapping
    public ResponseEntity<?> changetPassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        ChangePasswordResponse changePassword=  changePasswordService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(changePassword);
    }
}
