package com.critiquehub.dto.TagDto;

import com.critiquehub.dto.SpaceDto.SpaceResponseDto;

import java.util.List;

public record TagDto(
        Long id,
        String name,
        List<SpaceResponseDto> spaces
) { }
