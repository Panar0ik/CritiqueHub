package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Short representation of a message")
public record MessageShortDto(
        @Schema(description = "Message ID", example = "100")
        Long id
) { }
