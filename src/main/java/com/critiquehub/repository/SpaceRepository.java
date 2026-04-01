package com.critiquehub.repository;

import com.critiquehub.model.Space;
import com.critiquehub.model.User;
import com.critiquehub.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    List<Space> findByOwner(User owner);

    List<Space> findByTags(Tag tag);
    Optional<Space> findByName(String name);
}
