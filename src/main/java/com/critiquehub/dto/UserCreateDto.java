package com.critiquehub.dto;

public record UserCreateDto(
        String username,
        String email,
        String password
) { }
