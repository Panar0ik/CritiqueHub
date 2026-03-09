package com.critiquehub.mapper;

import com.critiquehub.dto.SpaceDto;
import com.critiquehub.model.Space;
import org.springframework.stereotype.Component;

@Component
public class SpaceMapper {

    public SpaceDto toDto(final Space spaceParam) {
        if (spaceParam == null) {
            return null;
        }

        return new SpaceDto(
                spaceParam.getId(),
                spaceParam.getName(),
                spaceParam.getDescription(),
                spaceParam.getCategory()
        );
    }
}
