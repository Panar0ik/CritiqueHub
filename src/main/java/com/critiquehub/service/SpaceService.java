package com.critiquehub.service;

import com.critiquehub.dto.SpaceDto;
import com.critiquehub.mapper.SpaceMapper;
import com.critiquehub.model.ChatMessage;
import com.critiquehub.model.Space;
import com.critiquehub.repository.CommunityRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class for handling community spaces and chat message business logic.
 */
@Service
public class SpaceService {

    /** The repository for accessing community and message data. */
    private final CommunityRepository communityRepository;

    /** The mapper for converting Space entities to DTOs. */
    private final SpaceMapper spaceMapper;

    /**
     * Constructs a new SpaceService with required dependencies.
     *
     * @param communityRepo the repository for community data
     * @param mapper the mapper for DTO conversion
     */
    public SpaceService(
            final CommunityRepository communityRepo,
            final SpaceMapper mapper
    ) {
        this.communityRepository = communityRepo;
        this.spaceMapper = mapper;
    }

    /**
     * Retrieves all community spaces and converts them to DTOs.
     *
     * @return a list of all available spaces as DTO objects
     */
    public List<SpaceDto> getAllSpaces() {
        return communityRepository.findAllSpaces().stream()
                .map(spaceMapper::toDto)
                .toList();
    }

    /**
     * Retrieves a community space by its ID and converts it to a DTO.
     *
     * @param id the unique identifier of the space
     * @return the space data transfer object
     * @throws ResponseStatusException if the space is not found
     */
    public SpaceDto getSpaceById(final Long id) {
        return communityRepository.findById(id)
                .map(spaceMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Space not found"
                ));
    }

    /**
     * Filters spaces by a given category.
     *
     * @param category the category string to match
     * @return a list of filtered spaces
     */
    public List<SpaceDto> getSpacesByCategory(final String category) {
        return communityRepository.findAllSpaces().stream()
                .filter(space -> space.category().equalsIgnoreCase(category))
                .map(spaceMapper::toDto)
                .toList();
    }

    /**
     * Finds the space by ID and adds a message to its internal collection.
     *
     * @param id      the identifier of the space
     * @param message the chat message to be added
     * @throws ResponseStatusException if the space is not found
     */
    public final void postMessage(final Long id, final ChatMessage message) {
        final Space space = communityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Space not found"
                ));

        space.addMessage(message);
    }

    /**
     * Retrieves all messages associated with a specific space.
     *
     * @param spaceId the ID of the space
     * @return a list of messages for the given space
     */
    public List<ChatMessage> getMessagesBySpace(final Long spaceId) {
        return communityRepository.findById(spaceId)
                .map(Space::messages)
                .orElse(new ArrayList<>());
    }

    /**
     * Creates and saves a new community space.
     *
     * @param spaceDto the data transfer object containing space details
     */
    public final void createSpace(final SpaceDto spaceDto) {
        final Space newSpace = new Space(
                spaceDto.id(),
                spaceDto.name(),
                spaceDto.description(),
                spaceDto.category(),
                new ArrayList<>()
        );

        communityRepository.save(newSpace);
    }
}
