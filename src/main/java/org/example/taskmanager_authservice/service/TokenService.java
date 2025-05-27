package org.example.taskmanager_authservice.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.entity.PasswordResetToken;
import org.example.taskmanager_authservice.entity.VerificationMailToken;
import org.example.taskmanager_authservice.repository.PasswordResetTokenRepository;
import org.example.taskmanager_authservice.repository.VerificationMailTokenRepository;
import org.example.taskmanager_authservice.security.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;
    private final VerificationMailTokenRepository verificationMailTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    public String getSubjectFromToken(String token) {
        Claims claims = jwtService.getClaims(token);
        return claims.getSubject();
    }

    public boolean isMailTokenValid(String token, String email) {
        return jwtService.isMailTokenValid(token, email);
    }

    public String saveVerificationMailToken(String email) {
        String token = jwtService.generateEmailVerificationToken(email);
        VerificationMailToken verificationmailToken = VerificationMailToken.builder()
                .email(email)
                .token(token)
                .build();

        verificationMailTokenRepository.save(verificationmailToken);

        return token;
    }

    public String savePasswordResetToken(String email) {
        String token = jwtService.generatePasswordResetToken(email);
        PasswordResetToken passwordResetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .build();

        passwordResetTokenRepository.save(passwordResetToken);

        return token;
    }

    public Optional<VerificationMailToken> findVerificationMailToken(String token) {
        return verificationMailTokenRepository.findByToken(token);
    }

    public Optional<PasswordResetToken> findPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void setVerificationMailTokenIsUsed(String email) {
        verificationMailTokenRepository.updateIsUsed(email);
    }

    public void setPasswordResetTokenRepositoryIsUsed(String email) {
        passwordResetTokenRepository.updateIsUsed(email);
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        return jwtService.isAccessTokenValid(token, userDetails);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return jwtService.isRefreshTokenValid(token, userDetails);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return jwtService.generateRefreshToken(userDetails);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return jwtService.generateAccessToken(userDetails);
    }




}
