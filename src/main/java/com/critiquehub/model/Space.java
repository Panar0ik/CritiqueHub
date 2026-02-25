package com.critiquehub.model;

import java.util.List;

/**
 * Represents a thematic interest group (Circle) where users discuss specific topics.
 */
public record Space(
        Long id,
        String name,
        String description,
        String category,
        List<String> members
) { }

/**
 * Represents a message sent within a specific Space.
 */
public record ChatMessage(
        Long id,
        Long spaceId,
        String sender,
        String content,
        java.time.LocalDateTime timestamp
) { }
