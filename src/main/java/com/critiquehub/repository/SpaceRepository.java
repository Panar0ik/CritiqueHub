package com.critiquehub.repository;

import com.critiquehub.model.Space;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"messages"})
    List<Space> findAll();

    @EntityGraph(attributePaths = {"messages", "messages.sender"})
    Optional<Space> findWithMessagesAndSendersById(Long id);
}
