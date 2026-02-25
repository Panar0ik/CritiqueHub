package com.critiquehub.model;

import java.time.LocalDateTime;

/**
 * Represents a message sent within a specific Space.
 *
 * @param id        the unique identifier of the message
 * @param sender    the username of the person who sent the message
 * @param content   the actual text content of the message
 * @param timestamp the date and time when the message was created
 */
public record ChatMessage(
        Long id,
        String sender,
        String content,
        LocalDateTime timestamp
) { }
