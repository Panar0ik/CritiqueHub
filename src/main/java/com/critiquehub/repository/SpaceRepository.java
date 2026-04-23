package com.critiquehub.repository;

import com.critiquehub.model.Space;
import com.critiquehub.model.User;
import com.critiquehub.model.Tag;
import io.micrometer.common.lang.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long> {
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"ow  ner", "tags"})
    List<Space> findAll();

    @EntityGraph(attributePaths = {"owner", "tags"})
    List<Space> findByOwner(User owner);

    @EntityGraph(attributePaths = {"owner", "tags"})
    List<Space> findByTags(Tag tag);

    @EntityGraph(attributePaths = {"owner", "tags"})
    @Query("SELECT DISTINCT s FROM Space s JOIN s.tags t WHERE t.name = :tagName")
    Page<Space> findByTagNameJPQL(@Param("tagName") String tagName, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT s.* FROM spaces s
        JOIN space_tags st ON s.id = st.space_id
        JOIN tags t ON st.tags_id = t.id
        WHERE t.name = :tagName
        """,
            countQuery = "SELECT count(*) FROM space_tags st JOIN tags t ON st.tags_id = t.id WHERE t.name = :tagName",
            nativeQuery = true)
    Page<Space> findByTagNameNative(@Param("tagName") String tagName, Pageable pageable);

    @EntityGraph(attributePaths = {"owner", "tags"})
    Optional<Space> findByName(String name);
}
