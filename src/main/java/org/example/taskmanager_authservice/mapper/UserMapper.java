package org.example.taskmanager_authservice.mapper;


import org.example.taskmanager_authservice.dto.UserDto;
import org.example.taskmanager_authservice.entity.User;

public class UserMapper {

    public UserDto toDto (User user){
        return UserDto.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .build();
    }

    public User toEntity (UserDto user){
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .build();
    }
}
