package org.example.taskmanager_authservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.ForgotPasswordRequest;
import org.example.taskmanager_authservice.dto.request.NewPasswordRequest;
import org.example.taskmanager_authservice.dto.response.ForgotPasswordResponse;
import org.example.taskmanager_authservice.service.ForgotPasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forgot")
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping
    public ResponseEntity<ForgotPasswordResponse> getForgotPassword(@Valid  @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
       ForgotPasswordResponse forgotPassword=  forgotPasswordService.getForgotPassword(forgotPasswordRequest);
       return ResponseEntity.ok(forgotPassword);
    }

    @GetMapping("/check/{token}")
    public ResponseEntity<ForgotPasswordResponse> checkLinkPassword(@PathVariable String token) {
        ForgotPasswordResponse forgotPassword=  forgotPasswordService.getNewPassword(token);
        return ResponseEntity.ok(forgotPassword);
    }

    @PostMapping("/newPassword")
    public ResponseEntity<ForgotPasswordResponse> setNewPassword(@Valid @RequestBody NewPasswordRequest newPasswordRequest) {
        ForgotPasswordResponse forgotPassword=  forgotPasswordService.setNewPassword(newPasswordRequest);
        return ResponseEntity.ok(forgotPassword);
    }

}
