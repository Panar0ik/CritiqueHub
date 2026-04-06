package com.critiquehub.mapper;

import com.critiquehub.dto.SpaceDto.SpaceResponseDto;
import com.critiquehub.model.Space;
import com.critiquehub.model.Tag;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SpaceMapper {

    public SpaceResponseDto toDto(final Space space) {
        if (space == null) {
            return null;
        }

        String ownerName;
        if (space.getOwner() != null) {
            ownerName = space.getOwner().getUsername();
        } else {
            ownerName = "Unknown";
        }

        Set<String> tagNames = space.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        return new SpaceResponseDto(
                space.getId(),
                space.getName(),
                space.getDescription(),
                ownerName,
                tagNames
        );
    }
}
