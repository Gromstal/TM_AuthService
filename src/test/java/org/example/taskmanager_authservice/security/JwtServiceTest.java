package org.example.taskmanager_authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("super_secret_key_123456789super");
    }

    @Test
    void checkAccessTokenGenerate() {
        String token = jwtService.generateAccessToken(getTestUserDetails());

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void checkAccessTokenContainUsernameAsSubject() {
        String token = jwtService.generateAccessToken(getTestUserDetails());
        String username = jwtService.extractSubject(token);

        assertEquals("username", username);
    }

    @Test
    void checkAccessTokenContainUsernameWrong() {
        String token = jwtService.generateAccessToken(getTestUserDetails());
        UserDetails otherUser = User.withUsername("otheruser").password("password").roles("USER").build();

        boolean isValid = jwtService.isAccessTokenValid(token, otherUser);

        assertFalse(isValid);
    }

    @Test
    void checkAccessTokenIsValid() {
        String token = jwtService.generateAccessToken(getTestUserDetails());
        boolean isValid = jwtService.isAccessTokenValid(token, getTestUserDetails());

        assertTrue(isValid);
    }

    @Test
    void checkAccessTokenIsNOTValid() {

        String expiredToken = Jwts
                .builder()
                .setSubject(getTestUserDetails().getUsername())
                .claim("type","access")
                .setIssuedAt(new Date(System.currentTimeMillis() - 20 * 60 * 1000))
                .setExpiration(new Date(System.currentTimeMillis() - 10 * 60 * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        boolean isValid = jwtService.isAccessTokenValid(expiredToken, getTestUserDetails());

        assertFalse(isValid);
    }

    @Test
    void checkAccessTokenClaim() {
        String token = jwtService.generateAccessToken(getTestUserDetails());
        Claims claims = jwtService.getClaims(token);

        assertEquals("access", claims.get("type"));
    }

    @Test
    void checkEmailVerificationTokenClaim() {
        String token = jwtService.generateEmailVerificationToken("email@example.com");
        Claims claims = jwtService.getClaims(token);

        assertEquals("email_verification", claims.get("type"));
        assertEquals("email@example.com", claims.getSubject());
    }

    @Test
    void checkRefreshTokenClaim() {
        String token = jwtService.generateRefreshToken(getTestUserDetails());
        Claims claims = jwtService.getClaims(token);

        assertEquals("refresh", claims.get("type"));
    }

    @Test
    void checkPasswordResetTokenClaim() {
        String token = jwtService.generatePasswordResetToken("email.com");
        Claims claims = jwtService.getClaims(token);

        assertEquals("password_reset", claims.get("type"));
    }
    @Test
    void checkInvalidSignature() {
        String invalidToken = Jwts.builder()
                .setSubject(getTestUserDetails().getUsername())
                .claim("type", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .signWith(getWrongSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(JwtException.class, () -> {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(invalidToken);
        });
    }


    private Key getSigningKey() {
        String secret = "super_secret_key_123456789super";
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getWrongSigningKey() {
        String secret = "super_secret_keyWrong1232222";
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private UserDetails getTestUserDetails() {
        return User.withUsername("username")
                .password("password")
                .roles("USER")
                .build();
    }
}