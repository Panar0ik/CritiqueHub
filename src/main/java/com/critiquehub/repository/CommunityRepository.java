package com.critiquehub.repository;

import org.springframework.stereotype.Repository;
import com.critiquehub.model.Space;
import com.critiquehub.model.ChatMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CommunityRepository {
    private final List<Space> spaces = new ArrayList<>(List.of(
            new Space(1L, "Cinema Critics", "Discussing arthouse movies", "Movies", new ArrayList<>())
    ));

    private final List<ChatMessage> messages = new ArrayList<>();

    public List<Space> findAllSpaces() {
        return spaces;
    }

    public void saveMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> findMessagesBySpace(Long spaceId) {
        return messages.stream()
                .filter(m -> m.spaceId().equals(spaceId))
                .collect(Collectors.toList());
    }
}
