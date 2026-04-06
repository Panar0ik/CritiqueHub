package com.critiquehub.mapper;

import com.critiquehub.dto.UserDto.UserCreateDto;
import com.critiquehub.dto.UserDto.UserResponseDto;
import com.critiquehub.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toDto(final User user) {
        if (user == null) {
            return null;
        }

        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }

    public User toEntity(final UserCreateDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        return user;
    }
}
