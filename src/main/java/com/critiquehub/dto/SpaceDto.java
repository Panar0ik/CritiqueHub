package com.critiquehub.dto;

/**
 * Data Transfer Object representing a community space
 * on the CritiqueHub platform.
 * Used to transfer space information between the service and controller layers.
 *
 * @param id          the unique identifier of the community space
 * @param name        the name of the interest group or space
 * @param description a brief description of what is discussed in this space
 * @param category    the thematic category (e.g., Movies, Books, Games)
 */
public record SpaceDto(
        Long id,
        String name,
        String description,
        String category
) { }
