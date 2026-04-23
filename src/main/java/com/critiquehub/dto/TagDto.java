package com.critiquehub.dto;

import java.util.List;

public record TagDto(
        Long id,
        String name,
        List<SpaceResponseDto> spaces
) { }
