package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for creating a new tag")
public record TagCreateDto(
        @Schema(description = "Tag name", example = "java")
        String name
) { }
