package com.critiquehub.dto.UserDto;

public record UserCreateDto(
        String username,
        String email,
        String password
) { }
