package com.critiquehub.mapper;

import org.springframework.stereotype.Component;
import com.critiquehub.model.Space;
import com.critiquehub.dto.SpaceDto;

/**
 * Mapper component used to convert Space entities into Data Transfer Objects.
 * This ensures internal domain models are not directly exposed to the client.
 */
@Component
public class SpaceMapper {

    /**
     * Converts a Space domain model to a SpaceDto.
     *
     * @param space the community space entity to be converted
     * @return the resulting data transfer object for the community platform
     */
    public SpaceDto toDto(final Space space) {
        return new SpaceDto(
                space.id(),
                space.name(),
                space.description(),
                space.category()
        );
    }
}
