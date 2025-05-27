package org.example.taskmanager_authservice.service;


import org.example.taskmanager_authservice.dto.request.AuthenticationRequest;
import org.example.taskmanager_authservice.dto.response.AuthenticationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

    @InjectMocks
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginService(authenticationManager, tokenService, userDetailsService);
    }

    @Test
    void checkSuccessLogin() {
        String expectedToken = "jwt-token";

        when(userDetailsService.loadUserByUsername(
                getSuccessTestUserDetails().getUsername()))
                .thenReturn(getSuccessTestUserDetails());

        when(tokenService.generateAccessToken(getSuccessTestUserDetails())).thenReturn(expectedToken);

        AuthenticationResponse response = loginService.login(authenticationRequest(getSuccessTestUserDetails()));

        assertEquals(expectedToken, response.getAccessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(getSuccessTestUserDetails().getUsername());
        verify(tokenService).generateAccessToken(getSuccessTestUserDetails());

    }

    @Test
    void checkUserNotFound() {
        when(userDetailsService.loadUserByUsername(
                getNegativeTestUserDetails().getUsername()))
                .thenThrow(new UsernameNotFoundException("Username not found"));

        assertThrows(UsernameNotFoundException.class, () -> loginService.login(authenticationRequest(getNegativeTestUserDetails())));

        verify(authenticationManager).authenticate(any());
        verify(userDetailsService).loadUserByUsername(getNegativeTestUserDetails().getUsername());

    }

    @Test
    void checkWrongPassword() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> loginService.login(authenticationRequest(getNegativeTestUserDetails())));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userDetailsService, tokenService);

    }


    private UserDetails getSuccessTestUserDetails() {
        return User.withUsername("username")
                .password("password")
                .roles("USER")
                .build();
    }

    private UserDetails getNegativeTestUserDetails() {
        return User.withUsername("wrong_username")
                .password("wrong_password")
                .roles("USER")
                .build();
    }

    private AuthenticationRequest authenticationRequest(UserDetails user) {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(user.getUsername());
        authenticationRequest.setPassword(user.getPassword());
        return authenticationRequest;
    }

}