package org.example.taskmanager_authservice.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

@Component
public class JwtService {

    private final String secret;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .claim("type", "access")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(15)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateEmailVerificationToken(String email) {
        return Jwts
                .builder()
                .setSubject(email)
                .claim("type", "email_verification")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusHours(1)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts
                .builder()
                .setSubject(userDetails.getUsername())
                .claim("type", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusDays(7)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generatePasswordResetToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "password_reset")
                .setIssuedAt(new Date())
                .setExpiration(Date.from(LocalDateTime.now().plusMinutes(15)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isAccessTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractSubject(token);
            return (username.equals(userDetails.getUsername()) && !isAccessTokenExpired(token));
        } catch (JwtException e) {
            return false;
        }

    }

    public boolean isMailTokenValid(String token, String email) {
        try {
            final String tokenMail = extractSubject(token);
            return (tokenMail.equals(email) && !isAccessTokenExpired(token));
        } catch (JwtException e) {
            return false;
        }

    }

    public String extractSubject(String token) {
        return getClaims(token).getSubject();
    }

    public Claims getClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            keyBytes = Arrays.copyOf(keyBytes, 32);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private boolean isAccessTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

}
