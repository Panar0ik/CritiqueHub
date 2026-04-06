package com.critiquehub.repository;

import com.critiquehub.model.Space;
import com.critiquehub.model.User;
import com.critiquehub.model.Tag;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"owner", "tags"})
    List<Space> findAll();

    @EntityGraph(attributePaths = {"owner", "tags"})
    List<Space> findByOwner(User owner);

    @EntityGraph(attributePaths = {"owner", "tags"})
    List<Space> findByTags(Tag tag);

    @EntityGraph(attributePaths = {"owner", "tags"})
    Optional<Space> findByName(String name);
}
