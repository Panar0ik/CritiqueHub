package com.critiquehub.mapper;

import org.springframework.stereotype.Component;
import com.critiquehub.model.Artwork;
import com.critiquehub.dto.ArtworkDto;

/**
 * Mapper component used to convert Artwork entities into Data Transfer Objects.
 */
@Component
public class ArtworkMapper {

    /**
     * Converts an Artwork domain model to an ArtworkDto.
     *
     * @param artwork the artwork entity to be converted
     * @return the resulting data transfer object
     */
    public ArtworkDto toDto(final Artwork artwork) {
        return new ArtworkDto(
                artwork.id(),
                artwork.title(),
                artwork.category(),
                artwork.creator()
        );
    }
}
