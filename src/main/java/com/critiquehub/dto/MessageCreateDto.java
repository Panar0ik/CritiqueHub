package com.critiquehub.dto;

public record MessageCreateDto(
        String Text,
        Long userId,
        Long spaceId
) { }
