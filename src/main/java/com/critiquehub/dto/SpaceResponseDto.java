package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(description = "Response containing space details")
public record SpaceResponseDto(
        @Schema(description = "Space unique ID", example = "5")
        Long id,

        @Schema(description = "Name of the space", example = "Tech Discussions")
        String name,

        @Schema(description = "Space description", example = "A place to discuss modern tech")
        String description,

        @Schema(description = "Username of the owner", example = "admin")
        String ownerUsername,

        @Schema(description = "Associated tags")
        Set<String> tagNames
) { }
