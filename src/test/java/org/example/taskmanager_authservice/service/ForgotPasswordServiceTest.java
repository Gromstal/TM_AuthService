package org.example.taskmanager_authservice.service;

import org.example.taskmanager_authservice.dto.UserDto;
import org.example.taskmanager_authservice.dto.request.ForgotPasswordRequest;
import org.example.taskmanager_authservice.dto.response.ForgotPasswordResponse;
import org.example.taskmanager_authservice.entity.User;
import org.example.taskmanager_authservice.mapper.UserMapper;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForgotPasswordServiceTest {

    @InjectMocks
    private ForgotPasswordService forgotPasswordService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private TokenService tokenService;

    private final UserMapper userMapper = new UserMapper();

    @Test
    void getForgotPassword() {
        UserDto userDto = new UserDto();
        userDto.setVerified(true);

        when(userRepository.findByUsername(getForgotPasswordRequest().getUsername())).thenReturn(Optional.of(userMapper.toEntity(userDto)));
        ForgotPasswordResponse response = forgotPasswordService.getForgotPassword(getForgotPasswordRequest());

        assertEquals("Password is reset. Please check your email to reset your password.", response.getMessage());
        verify(emailService).sendVerificationEmail(any(), any());
        verify(tokenService).savePasswordResetToken(any());
    }

    @Test
    void getForgotPasswordUserNotFound() {
        UserDto userDto = new UserDto();

        when(userRepository.findByUsername(getForgotPasswordRequest().getUsername())).thenReturn(Optional.of(userMapper.toEntity(userDto)));
        ForgotPasswordResponse response = forgotPasswordService.getForgotPassword(getForgotPasswordRequest());

        assertEquals("User not found. Please try again.", response.getMessage());
        verify(emailService, never()).sendVerificationEmail(any(), any());
        verify(tokenService,never()).savePasswordResetToken(any());
    }

    @Test
    void getNewPassword() {
    }

    @Test
    void setNewPassword() {
    }

    private ForgotPasswordRequest getForgotPasswordRequest() {
        return ForgotPasswordRequest.builder()
                .username("pashka")
                .build();
    }
}