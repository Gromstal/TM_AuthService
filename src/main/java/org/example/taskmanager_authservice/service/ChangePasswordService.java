package org.example.taskmanager_authservice.service;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager_authservice.dto.request.ChangePasswordRequest;
import org.example.taskmanager_authservice.dto.response.ChangePasswordResponse;
import org.example.taskmanager_authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChangePasswordService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordService.class);

    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {
        String username = tokenService.getSubjectFromToken(request.getToken());
        log.info("Changing password for user: {}", username);

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!tokenService.isAccessTokenValid(request.getToken(), userDetails)) {
                log.warn("Invalid access token");
                return new ChangePasswordResponse("Access token is invalid or expired");
            }

            if (!userService.checkPassword(request.getOldPassword(), userDetails.getPassword())) {
                log.warn("Old password does not match");
                return new ChangePasswordResponse("Old password is incorrect");
            }
        } catch (UsernameNotFoundException e) {
            log.error("Username not found {}", username);
            return new ChangePasswordResponse("User not found");
        }
        String encodedNewPassword = userService.encodePassword(request.getNewPassword());
        userRepository.updatePasswordByUsername(username, encodedNewPassword);
        log.info("Password changed successfully for user {}", username);

        return new ChangePasswordResponse("Password changed successfully");
    }
}
