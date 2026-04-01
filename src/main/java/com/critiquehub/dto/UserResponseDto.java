package com.critiquehub.dto;

public record UserResponseDto(
        Long id,
        String username,
        String email
) { }
