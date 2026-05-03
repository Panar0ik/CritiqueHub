package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Schema(description = "Request for creating a new space")
public record SpaceCreateDto(
        @NotBlank(message = "Name is required")
        @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = "Name must be between 2 and 100 characters")
        @Schema(description = "Unique name of the space", example = "Tech Discussions")
        String name,

        @Size(max = MAX_DESC_LENGTH, message = "Description must not exceed 1000 characters")
        @Schema(description = "Optional space description", example = "A place to discuss modern tech")
        String description,

        @NotNull(message = "Owner ID must not be null")
        @Schema(description = "ID of the user creating the space", example = "1")
        Long ownerId,

        @Schema(description = "Set of tag names associated with the space", example = "[\"java\", \"backend\"]")
        Set<String> tagNames
) {
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESC_LENGTH = 1000;
}
