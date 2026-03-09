package com.critiquehub.repository;

import com.critiquehub.model.Space;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {

    @Override
    @EntityGraph(attributePaths = {"messages"})
    List<Space> findAll();
}
