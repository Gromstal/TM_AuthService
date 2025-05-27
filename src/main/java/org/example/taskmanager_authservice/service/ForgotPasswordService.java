package org.example.taskmanager_authservice.service;


import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.ForgotPasswordRequest;
import org.example.taskmanager_authservice.dto.request.NewPasswordRequest;
import org.example.taskmanager_authservice.dto.response.ForgotPasswordResponse;
import org.example.taskmanager_authservice.entity.User;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TokenService tokenService;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(ForgotPasswordService.class);

    public ForgotPasswordResponse getForgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        log.info("Forgot password request received for username: {}", forgotPasswordRequest.getUsername());

        return userRepository.findByUsername(forgotPasswordRequest.getUsername())
                .filter(User::isVerified)
                .map(user -> {
                    String token = tokenService.savePasswordResetToken(user.getEmail());
                    log.info("Password reset token generated for email: {}", user.getEmail());

                    emailService.sendVerificationEmail(user.getEmail(), token);
                    log.info("Password reset email sent to: {}", user.getEmail());

                    return new ForgotPasswordResponse("Password is reset. Please check your email to reset your password.");
                })
                .orElseGet(() -> {
                    log.warn("Forgot password attempt failed: user {} not found or not verified", forgotPasswordRequest.getUsername());
                    return new ForgotPasswordResponse("User not found. Please try again.");
                });
    }

    public ForgotPasswordResponse getNewPassword(String token) {
        log.info("Get new password request with token: {}", token);

        return tokenService.findPasswordResetToken(token)
                .filter(prtoken -> !prtoken.isUsed())
                .map(prtoken -> {
                    if (tokenService.isMailTokenValid(token, prtoken.getEmail())) {
                        log.info("Password reset token valid for email: {}", prtoken.getEmail());
                        return new ForgotPasswordResponse("Now you set your new password.");
                    } else {
                        log.warn("Password reset token expired for email: {}", prtoken.getEmail());
                        return new ForgotPasswordResponse("Your password link is expired. Please try again.");
                    }
                })
                .orElseGet(() -> {
                    log.warn("Password reset token is already used or invalid: {}", token);
                    return new ForgotPasswordResponse("Your link is already used. Please try again.");
                });
    }

    @Transactional
    public ForgotPasswordResponse setNewPassword(NewPasswordRequest newPasswordRequest) {
        log.info("Set new password request received for token: {}", newPasswordRequest.getToken());

        if (!newPasswordRequest.getNewPassword().equals(newPasswordRequest.getConfirmPassword())) {
            log.warn("Passwords do not match for token: {}", newPasswordRequest.getToken());
            return new ForgotPasswordResponse("Passwords do not match. Please try again.");
        }

        return tokenService.findPasswordResetToken(newPasswordRequest.getToken())
                .filter(token -> tokenService.isMailTokenValid(newPasswordRequest.getToken(), token.getEmail()))
                .map(token -> {
                    String newPassword = userService.encodePassword(newPasswordRequest.getNewPassword());
                    userRepository.updatePasswordByEmail(token.getEmail(), newPassword);
                    tokenService.setPasswordResetTokenRepositoryIsUsed(token.getEmail());
                    log.info("Password successfully changed for email: {}", token.getEmail());
                    return new ForgotPasswordResponse("Your new password has been successfully changed.");
                })
                .orElseGet(() -> {
                    log.warn("Invalid or expired token when setting new password: {}", newPasswordRequest.getToken());
                    return new ForgotPasswordResponse("Token is invalid or expired.");
                });
    }
}

