package com.critiquehub.dto;

import java.util.Set;

public record SpaceResponseDto(
        Long id,
        String name,
        String description,
        String ownerUsername,
        Set<String> tagNames
) { }
