package org.example.taskmanager_authservice.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.ForgotPasswordRequest;
import org.example.taskmanager_authservice.dto.request.NewPasswordRequest;
import org.example.taskmanager_authservice.dto.response.ForgotPasswordResponse;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final UserService userService;

    public ForgotPasswordResponse getForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        return userRepository.findByUsername(forgotPasswordRequest.getUsername())
                .map(user -> {
                    String token = tokenService.savePasswordResetToken(user.getEmail());
                    emailService.sendVerificationEmail(user.getEmail(), token);
                    return new ForgotPasswordResponse("Password is reset. Please check your email to reset your password.");
                })
                .orElse(new ForgotPasswordResponse("User not found. Please try again."));
    }

    public ForgotPasswordResponse getNewPassword(String token) {
        return tokenService.findPasswordResetToken(token)
                .filter(prtoken -> !prtoken.isUsed())
                .map(prtoken -> {
                    if (tokenService.isMailTokenValid(token, prtoken.getEmail())) {
                        return new ForgotPasswordResponse("Now you set your new password.");
                    }
                    return new ForgotPasswordResponse("Your password link is expired. Please try again.");
                })
                .orElse(new ForgotPasswordResponse("Your link is already used. Please try again."));
    }

    @Transactional
    public ForgotPasswordResponse setNewPassword(NewPasswordRequest newPasswordRequest) {
        if (!newPasswordRequest.getNewPassword().equals(newPasswordRequest.getConfirmPassword())) {
            return new ForgotPasswordResponse("Passwords do not match. Please try again.");
        }

        return tokenService.findPasswordResetToken(newPasswordRequest.getToken())
                .filter(token -> tokenService.isMailTokenValid(newPasswordRequest.getToken(), token.getEmail()))
                .map(token -> {
                    String newPassword = userService.getNewPassword(newPasswordRequest.getNewPassword());
                    userRepository.updatePassword(token.getEmail(), newPassword);
                    tokenService.setPasswordResetTokenRepositoryIsUsed(token.getEmail());
                    return new ForgotPasswordResponse("Your new password has been successfully changed.");
                })
                .orElse(new ForgotPasswordResponse("Token is invalid or expired."));
    }
}
