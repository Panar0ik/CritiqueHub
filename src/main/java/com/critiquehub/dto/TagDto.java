package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Data Transfer Object for Tags")
public record TagDto(
        @Schema(description = "Tag unique ID", example = "1")
        Long id,

        @Schema(description = "Tag name", example = "java")
        String name,

        @Schema(description = "List of spaces associated with this tag")
        List<SpaceResponseDto> spaces
) { }
