package org.example.taskmanager_authservice.service;

import org.example.taskmanager_authservice.dto.request.TokenRequest;
import org.example.taskmanager_authservice.dto.response.TokenResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshServiceTest {

    @InjectMocks
    private RefreshService refreshService;
    @Mock
    private  TokenService tokenService;
    @Mock
    private  UserDetailsService userDetailsService;

    @Test
    void checkRefreshTokenValid() {
        String refreshToken = "validRefreshToken";
        String username = "user1";
        UserDetails userDetails = mock(UserDetails.class);
        String newAccessToken = "newAccessToken";

        when(tokenService.getSubjectFromToken(refreshToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(tokenService.isRefreshTokenValid(refreshToken, userDetails)).thenReturn(true);
        when(tokenService.generateAccessToken(userDetails)).thenReturn(newAccessToken);

        TokenRequest tokenRequest = new TokenRequest(refreshToken);
        TokenResponse response = refreshService.getNewAccessToken(tokenRequest);

        assertEquals(newAccessToken, response.getToken());
        assertNull(response.getMessage());

        verify(tokenService).getSubjectFromToken(refreshToken);
        verify(userDetailsService).loadUserByUsername(username);
        verify(tokenService).isRefreshTokenValid(refreshToken, userDetails);
        verify(tokenService).generateAccessToken(userDetails);
    }

    @Test
    void checkRefreshTokenInvalid() {
        String refreshToken = "invalidRefreshToken";
        String username = "user2";
        UserDetails userDetails = mock(UserDetails.class);

        when(tokenService.getSubjectFromToken(refreshToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(tokenService.isRefreshTokenValid(refreshToken, userDetails)).thenReturn(false);

        TokenRequest tokenRequest = new TokenRequest(refreshToken);
        TokenResponse response = refreshService.getNewAccessToken(tokenRequest);

        assertNull(response.getToken());
        assertEquals("You need to login", response.getMessage());

        verify(tokenService).getSubjectFromToken(refreshToken);
        verify(userDetailsService).loadUserByUsername(username);
        verify(tokenService).isRefreshTokenValid(refreshToken, userDetails);
        verify(tokenService, never()).generateAccessToken(any());
    }

    @Test
    void checkUsernameNotFoundException() {
        String refreshToken = "someRefreshToken";
        String username = "username";

        when(tokenService.getSubjectFromToken(refreshToken)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenThrow(new UsernameNotFoundException(username));

        TokenRequest tokenRequest = new TokenRequest(refreshToken);
        TokenResponse response = refreshService.getNewAccessToken(tokenRequest);

        assertNull(response.getToken());
        assertEquals("User not found", response.getMessage());

        verify(tokenService).getSubjectFromToken(refreshToken);
        verify(userDetailsService).loadUserByUsername(username);
        verify(tokenService, never()).isRefreshTokenValid(any(), any());
        verify(tokenService, never()).generateAccessToken(any());
    }
}