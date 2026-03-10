package com.critiquehub.mapper;

import com.critiquehub.dto.SpaceDto;
import com.critiquehub.model.Space;
import org.springframework.stereotype.Component;

@Component
public class SpaceMapper {

    public SpaceDto toDto(final Space space) {
        if (space == null) {
            return null;
        }
        return new SpaceDto(
                space.getId(),
                space.getName(),
                space.getDescription(),
                space.getCategory()
        );
    }

    public Space toEntity(final SpaceDto dto) {
        if (dto == null) {
            return null;
        }
        Space space = new Space();
        space.setName(dto.name());
        space.setDescription(dto.description());
        space.setCategory(dto.category());
        return space;
    }
}
