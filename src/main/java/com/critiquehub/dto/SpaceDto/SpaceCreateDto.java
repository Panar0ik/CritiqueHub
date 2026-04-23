package com.critiquehub.dto.SpaceDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record SpaceCreateDto(
        @NotBlank(message = "Name is required")
        String name,
        String description,
        @NotNull(message = "Owner ID must not be null")
        Long ownerId,
        Set<String> tagNames
) { }
