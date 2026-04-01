package com.critiquehub.dto;

import java.util.Set;

public record SpaceCreateDto(
        String name,
        String description,
        Long ownerId,
        Set<String> tagNames
) { }
