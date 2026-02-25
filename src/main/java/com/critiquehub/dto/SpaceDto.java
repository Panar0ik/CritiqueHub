package com.critiquehub.dto;

/**
 * Data Transfer Object representing an artwork on the CritiqueHub platform.
 */
public record ArtworkDto(
        Long id,
        String title,
        String category,
        String creator
) { }
