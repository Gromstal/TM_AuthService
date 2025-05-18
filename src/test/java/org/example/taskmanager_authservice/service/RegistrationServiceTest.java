package org.example.taskmanager_authservice.service;


import org.example.taskmanager_authservice.dto.request.RegistrationRequest;
import org.example.taskmanager_authservice.dto.response.RegistrationResponse;
import org.example.taskmanager_authservice.entity.User;
import org.example.taskmanager_authservice.entity.VerificationMailToken;
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
class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Test
    void checkEmailAlreadyInUse() {
        when(userRepository.findByEmail(getRegistrationRequest().getEmail()))
                .thenReturn(Optional.of(new User()));
        RegistrationResponse expectedResponse = new RegistrationResponse("Email is already in use");
        RegistrationResponse actualResponse = registrationService.startRegistration(getRegistrationRequest());

        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
        verify(emailService, never()).sendVerificationEmail(any(), any());
    }

    @Test
    void checkSendVerificationEmail() {
        RegistrationRequest request = getRegistrationRequest();

        when(userRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.empty());
        when(userService.createUser(request)).thenReturn(new User());
        when(tokenService.saveVerificationMailToken(request.getEmail())).thenReturn("token");
        when(emailService.sendVerificationEmail(request.getEmail(), "token")).thenReturn(new RegistrationResponse("Registration successful. Please check your email to verify your account."));

        RegistrationResponse expectedResponse = new RegistrationResponse("Registration successful. Please check your email to verify your account.");
        RegistrationResponse actualResponse = registrationService.startRegistration(request);

        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(emailService).sendVerificationEmail(eq(request.getEmail()), eq("token"));
        verify(tokenService).saveVerificationMailToken(request.getEmail());
        verify(userRepository).findByEmail(request.getEmail());
        verify(userService).createUser(request);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void confirmRegistrationTokenIsEmptyOrUsed() {
        when(tokenService.findVerificationMailToken(any())).thenReturn(Optional.empty());
        RegistrationResponse expectedResponse = new RegistrationResponse("Verification mail token is empty or used");
        RegistrationResponse actualResponse = registrationService.confirmRegistration("token");

        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
    }

    @Test
    void confirmRegistrationTokenIsAlreadyUsed() {
        VerificationMailToken usedToken = mock(VerificationMailToken.class);
        when(usedToken.isUsed()).thenReturn(true);
        when(tokenService.findVerificationMailToken(any())).thenReturn(Optional.of(usedToken));

        RegistrationResponse expected = new RegistrationResponse("Verification mail token is empty or used");
        RegistrationResponse actual = registrationService.confirmRegistration("someToken");

        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    void confirmRegistrationTokenInvalidJwt() {
        String token = "someToken";
        VerificationMailToken unusedToken = mock(VerificationMailToken.class);
        when(unusedToken.isUsed()).thenReturn(false);
        when(tokenService.findVerificationMailToken(token)).thenReturn(Optional.of(unusedToken));
        when(tokenService.isMailTokenValid(token, unusedToken.getEmail())).thenReturn(false);

        RegistrationResponse expected = new RegistrationResponse("Verification mail token expired");
        RegistrationResponse actual = registrationService.confirmRegistration(token);

        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    void confirmRegistrationTokenValidAndSuccess() {
        String token = "validToken";
        VerificationMailToken unusedToken = mock(VerificationMailToken.class);
        when(unusedToken.isUsed()).thenReturn(false);
        when(unusedToken.getEmail()).thenReturn("user@example.com");

        when(tokenService.findVerificationMailToken(token)).thenReturn(Optional.of(unusedToken));
        when(tokenService.isMailTokenValid(token, unusedToken.getEmail())).thenReturn(true);

        RegistrationResponse expected = new RegistrationResponse("Verification is confirmed. Now you can login");
        RegistrationResponse actual = registrationService.confirmRegistration(token);

        assertEquals(expected.getMessage(), actual.getMessage());
        verify(tokenService).setVerificationMailTokenIsUsed(unusedToken.getEmail());
    }


    private RegistrationRequest getRegistrationRequest() {
        return RegistrationRequest.builder()
                .username("username")
                .password("password")
                .email("email@gmail.com")
                .build();
    }
}
