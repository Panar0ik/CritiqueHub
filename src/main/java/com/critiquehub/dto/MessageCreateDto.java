package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request for sending a new message")
public record MessageCreateDto(
        @Schema(description = "Text content of the message", example = "Hello, world!")
        String text,

        @Schema(description = "ID of the author", example = "1")
        Long userId,

        @Schema(description = "ID of the space where the message is sent", example = "5")
        Long spaceId
) { }
