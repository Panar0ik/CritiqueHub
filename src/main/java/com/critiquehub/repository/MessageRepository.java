package com.critiquehub.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.critiquehub.model.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @EntityGraph(attributePaths = {"attachments", "tags"})
    List<Message> findAllBySpaceId(Long spaceId);
}
