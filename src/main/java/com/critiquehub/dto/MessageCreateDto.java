package com.critiquehub.dto;

public record MessageCreateDto(
        String text,
        Long userId,
        Long spaceId
) { }
