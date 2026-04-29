package com.critiquehub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record SpaceCreateDto(
        @NotBlank(message = "Name is required")
        @Size(min = MIN_NAME_LENGTH, max = MAX_NAME_LENGTH, message = "Name must be between 2 and 100 characters")
        String name,

        @Size(max = MAX_DESC_LENGTH, message = "Description must not exceed 1000 characters")
        String description,

        @NotNull(message = "Owner ID must not be null")
        Long ownerId,

        Set<String> tagNames
) {
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_DESC_LENGTH = 1000;
}
