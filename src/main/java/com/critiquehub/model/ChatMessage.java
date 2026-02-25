package com.critiquehub.model;

import java.util.List;
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
