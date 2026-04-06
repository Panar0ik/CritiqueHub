package com.critiquehub.dto.MessageDto;

public record MessageCreateDto(
        String text,
        Long userId,
        Long spaceId
) { }
