package com.critiquehub.model;

import java.util.List;

/**
 * Represents a thematic interest group (Circle)
 * where users discuss specific topics.
 *
 * @param id          the unique identifier of the space
 * @param name        the name of the interest group
 * @param description a brief summary of the space's purpose
 * @param category    the thematic category (e.g., Movies, Books)
 * @param messages     the list of messages belonging to this space
 */
public record Space(
        Long id,
        String name,
        String description,
        String category,
        List<ChatMessage> messages
) {
    /**
     * Adds a new chat message to the internal list of messages.
     *
     * @param message the chat message object to be added
     */
    public void addMessage(final ChatMessage message) {
        this.messages.add(message);
    }
}
