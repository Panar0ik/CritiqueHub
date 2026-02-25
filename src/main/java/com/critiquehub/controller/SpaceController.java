package com.critiquehub.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import com.critiquehub.dto.SpaceDto;
import com.critiquehub.model.ChatMessage;
import com.critiquehub.service.SpaceService;

import java.util.List;

/**
 * REST Controller for managing community spaces and interactions.
 * Provides endpoints for retrieving spaces and managing chat messages.
 */
@RestController
@RequestMapping("/api/community")
public class SpaceController {

    /** The service layer for community space logic. */
    @Autowired
    private SpaceService spaceService;

    /**
     * Retrieves all available interest groups (spaces).
     *
     * @return a list of all community spaces as DTOs
     */
    @GetMapping("/spaces")
    public List<SpaceDto> getAllSpaces() {
        return spaceService.getAllSpaces();
    }

    /**
     * Endpoint with @PathVariable: Retrieves a specific space by its ID.
     * Usage: GET /api/community/spaces/1
     *
     * @param id the unique identifier of the space to retrieve
     * @return the space data transfer object
     */
    @GetMapping("/spaces/{id}")
    public SpaceDto getSpace(
            @PathVariable final Long id) {
        return spaceService.getSpaceById(id);
    }

    /**
     * Retrieves chat history for a specific space.
     *
     * @param id the unique identifier of the space
     * @return a list of messages belonging to the space
     */
    @GetMapping("/spaces/{id}/messages")
    public List<ChatMessage> getMessages(@PathVariable final Long id) {
        return spaceService.getMessagesBySpace(id);
    }

    /**
     * Sends a new message to a specific space.
     *
     * @param id      the unique identifier of the space
     * @param message the chat message data received from the client
     */
    @PostMapping("/spaces/{id}/messages")
    public final void sendMessage(
            @PathVariable final Long id,
            @RequestBody final ChatMessage message) {
        spaceService.postMessage(id, message);
    }

    /**
     * Endpoint with @RequestParam: Searches for spaces by a category filter.
     * Usage: GET /api/community/search?category=Movies
     *
     * @param category the category name to filter by
     * @return a list of spaces matching the category
     */
    @GetMapping("/search")
    public List<SpaceDto> findSpacesByCategory(
            @RequestParam final String category) {
        return spaceService.getSpacesByCategory(category);
    }

    /**
     * Creates a new community space.
     *
     * @param spaceDto the space data transfer object from the client
     */
    @PostMapping("/spaces")
    public final void addSpace(@RequestBody final SpaceDto spaceDto) {
        spaceService.createSpace(spaceDto);
    }
}
