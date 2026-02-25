package com.critiquehub.repository;

import org.springframework.stereotype.Repository;
import com.critiquehub.model.Space;
import com.critiquehub.model.ChatMessage;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for managing in-memory data
 * for community spaces and messages.
 */
@Repository
public class CommunityRepository {

    /** In-memory storage for community spaces. */
    @SuppressWarnings("checkstyle:LineLength")
    private final List<Space> spaces = new ArrayList<>(List.of(
            new Space(
                    1L,
                    "Cinema Critics",
                    "Discussing arthouse movies",
                    "Movies",
                    new ArrayList<>(List.of(
                            new ChatMessage(
                                    101L,
                                    "cine_lover",
                                    "Has anyone seen the Wes Anderson movies?",
                                    LocalDateTime.now().minusHours(2)
                            ),
                            new ChatMessage(
                                    102L,
                                    "film_buff",
                                    "Yes! It's amazing.",
                                    LocalDateTime.now().minusHours(1)
                            )
                    ))
            ),
            new Space(
                    2L,
                    "Elden Ring Fans",
                    "Hardcore souls-like discussions",
                    "Games",
                    new ArrayList<>(List.of(
                            new ChatMessage(
                                    103L,
                                    "tarnished_one",
                                    "I finally beat Malenia after 50 tries!",
                                    LocalDateTime.now().minusMinutes(30)
                            )
                    ))
            ),
            new Space(
                    3L,
                    "Metal Gear Solid",
                    "Third-person stealth action",
                    "Games",
                    new ArrayList<>()
            )
    ));

    /**
     * Retrieves all available community spaces.
     *
     * @return a list of all spaces
     */
    public List<Space> findAllSpaces() {
        return new ArrayList<>(spaces);
    }

    /**
     * Finds a specific space by its unique identifier.
     *
     * @param id the unique ID of the space
     * @return an Optional containing the found space, or empty if not found
     */
    public Optional<Space> findById(final Long id) {
        return spaces.stream()
                .filter(space -> space.id().equals(id))
                .findFirst();
    }

    /**
     * Saves a new community space to the in-memory storage.
     *
     * @param space the space entity to be saved
     */
    public final void save(final Space space) {
        spaces.add(space);
    }
}
