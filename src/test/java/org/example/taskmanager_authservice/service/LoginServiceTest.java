package org.example.taskmanager_authservice.service;


import org.example.taskmanager_authservice.dto.request.AuthenticationRequest;
import org.example.taskmanager_authservice.dto.response.AuthenticationResponse;
import org.example.taskmanager_authservice.entity.User;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(authenticationManager, tokenService, userDetailsService,userRepository);
    }

    @Test
    void checkSuccessLogin() {
        UserDetails userDetails = getSuccessTestUserDetails();
        AuthenticationRequest request = authenticationRequest(userDetails);
        String expectedAccessToken = "jwt-token";
        String expectedRefreshToken = "refresh-token";

        when(userRepository.findByUsername(userDetails.getUsername()))
                .thenReturn(Optional.of(getUser()));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        when(userDetailsService.loadUserByUsername(userDetails.getUsername()))
                .thenReturn(userDetails);

        when(tokenService.generateAccessToken(userDetails)).thenReturn(expectedAccessToken);
        when(tokenService.generateRefreshToken(userDetails)).thenReturn(expectedRefreshToken);


        AuthenticationResponse response = loginService.login(request);

        assertEquals(expectedAccessToken, response.getAccessToken());
        assertEquals(expectedRefreshToken, response.getRefreshToken());

        verify(userRepository).findByUsername(userDetails.getUsername());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(userDetails.getUsername());
        verify(tokenService).generateAccessToken(userDetails);
        verify(tokenService).generateRefreshToken(userDetails);

    }

    @Test
    void checkUnverifiedUser() {

        AuthenticationRequest request = authenticationRequest(getSuccessTestUserDetails());

        when(userRepository.findByUsername(getSuccessTestUserDetails().getUsername()))
                .thenReturn(Optional.of(getUnverifiedUser()));


        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () -> {
            loginService.login(request);
        });

        assertEquals("User is not verified", thrown.getMessage());

        verify(authenticationManager, never()).authenticate(any());
        verifyNoInteractions(userDetailsService, tokenService);
    }

    @Test
    void checkUserNotFound() {
        AuthenticationRequest request = authenticationRequest(getNegativeTestUserDetails());

        when(userRepository.findByUsername(getNegativeTestUserDetails().getUsername()))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> loginService.login(request));

        verify(userRepository).findByUsername(getNegativeTestUserDetails().getUsername());
        verifyNoInteractions(authenticationManager, userDetailsService, tokenService);

    }

    @Test
    void checkWrongPassword() {

        UserDetails userDetails = getSuccessTestUserDetails();

        when(userRepository.findByUsername(userDetails.getUsername()))
                .thenReturn(Optional.of(getUser()));

        when(userRepository.findByUsername(userDetails.getUsername()))
                .thenReturn(Optional.of(getUser()));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> loginService.login(authenticationRequest(getNegativeTestUserDetails())));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userDetailsService, tokenService);

    }


    private UserDetails getSuccessTestUserDetails() {
        return User.builder().
                username("wrong_username")
                .password("password")
                .role("USER")
                .build();
    }

    private UserDetails getNegativeTestUserDetails() {
        return User.builder().
                username("wrong_username")
                .password("wrong_password")
                .role("USER")
                .build();
    }

    private User getUser(){
        User userEntity = new User();
        userEntity.setUsername(getSuccessTestUserDetails().getUsername());
        userEntity.setPassword(getSuccessTestUserDetails().getPassword());
        userEntity.setVerified(true);
        return userEntity;
    }

    private User getUnverifiedUser(){
        User unverifiedUser = new User();
        unverifiedUser.setUsername(getSuccessTestUserDetails().getUsername());
        unverifiedUser.setVerified(false);
        return unverifiedUser;
    }

    private AuthenticationRequest authenticationRequest(UserDetails user) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(user.getUsername());
        authenticationRequest.setPassword(user.getPassword());
        return authenticationRequest;
    }

}