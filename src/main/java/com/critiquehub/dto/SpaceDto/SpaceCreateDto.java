package com.critiquehub.dto.SpaceDto;

import java.util.Set;

public record SpaceCreateDto(
        String name,
        String description,
        Long ownerId,
        Set<String> tagNames
) { }
