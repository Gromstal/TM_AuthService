package org.example.taskmanager_authservice.service;

import org.example.taskmanager_authservice.dto.request.ChangePasswordRequest;
import org.example.taskmanager_authservice.dto.response.ChangePasswordResponse;
import org.example.taskmanager_authservice.entity.User;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChangePasswordServiceTest {

    @InjectMocks
    private ChangePasswordService changePasswordService;

    @Mock
    private TokenService tokenService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserService userService;

    @Test
    void changePasswordPositive() {

        ChangePasswordRequest request = getRequest();
        User user = getUser();

        when(tokenService.getSubjectFromToken(request.getToken())).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(user);
        when(userService.checkPassword(request.getOldPassword(), "oldPassword")).thenReturn(true);
        when(userService.encodePassword(request.getNewPassword())).thenReturn("encodedPassword");
        when(tokenService.isAccessTokenValid(request.getToken(), user)).thenReturn(true);

        ChangePasswordResponse expectedResult = new ChangePasswordResponse("Password changed successfully");
        ChangePasswordResponse actual = changePasswordService.changePassword(getRequest());

        assertEquals(expectedResult, actual);
        verify(userRepository).updatePasswordByUsername(eq(user.getUsername()), any());

    }
    @Test
    void changePasswordNoUser() {

        ChangePasswordRequest request = getRequest();

        when(tokenService.getSubjectFromToken(request.getToken())).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenThrow(new UsernameNotFoundException("User not found"));


        ChangePasswordResponse expectedResult = new ChangePasswordResponse("User not found");
        ChangePasswordResponse actual = changePasswordService.changePassword(getRequest());

        assertEquals(expectedResult, actual);
    }

    @Test
    void changePasswordAccessTokenIsInvalid() {

        ChangePasswordRequest request = getRequest();

        when(tokenService.getSubjectFromToken(request.getToken())).thenReturn("username");

        ChangePasswordResponse expectedResult = new ChangePasswordResponse("Access token is invalid or expired");
        ChangePasswordResponse actual = changePasswordService.changePassword(getRequest());

        assertEquals(expectedResult, actual);
    }

    @Test
    void changePasswordMismatched() {

        ChangePasswordRequest request = getRequest();
        User user = getUser();

        when(tokenService.getSubjectFromToken(request.getToken())).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(user);
        when(userService.checkPassword(request.getOldPassword(), "oldPassword")).thenReturn(false);
        when(tokenService.isAccessTokenValid(request.getToken(), user)).thenReturn(true);

        ChangePasswordResponse expectedResult = new ChangePasswordResponse("Old password is incorrect");
        ChangePasswordResponse actual = changePasswordService.changePassword(getRequest());

        assertEquals(expectedResult, actual);
    }

    private ChangePasswordRequest getRequest() {
        return ChangePasswordRequest.builder()
                .token("token")
                .newPassword("newPassword")
                .oldPassword("oldPassword")
                .build();
    }

    private User getUser() {
        return User.builder()
                .username("username")
                .password("oldPassword")
                .build();
    }
}