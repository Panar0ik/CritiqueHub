package com.critiquehub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.critiquehub.dto.ArtworkDto;
import com.critiquehub.mapper.ArtworkMapper;
import com.critiquehub.repository.ArtworkRepository;

/**
 * Service class for handling artwork business logic.
 */
@Service
public class ArtworkService {

    @Autowired
    private ArtworkRepository artworkRepository;

    @Autowired
    private ArtworkMapper artworkMapper;

    /**
     * Retrieves an artwork by its ID and converts it to a DTO.
     *
     * @param id the unique identifier of the artwork
     * @return the artwork data transfer object
     * @throws ResponseStatusException if the artwork is not found
     */
    public ArtworkDto getArtworkById(final Long id) {
        return artworkRepository.findById(id)
                .map(artworkMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artwork not found"));
    }
}
