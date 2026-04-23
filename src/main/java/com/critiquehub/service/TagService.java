package com.critiquehub.service;

import com.critiquehub.dto.TagDto;
import com.critiquehub.model.Space;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.TagRepository;
import com.critiquehub.model.Tag;
import com.critiquehub.util.cache.SpaceCacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final SpaceRepository spaceRepository;
    private final SpaceCacheService spaceCacheService;

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tag getByName(final String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + name));
    }

    @Transactional
    public Tag saveTag(final TagDto tagDto) {
        String cleanName = tagDto.name();

        return tagRepository.findByName(cleanName)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(cleanName);
                    return tagRepository.save(tag);
                });
    }

    @Transactional
    public Tag updateTagName(final Long id, final String newName) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + id));

        tagRepository.findByName(newName).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new EntityNotFoundException("Tag with name '" + newName + "' already exists");
            }
        });

        tag.setName(newName);
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(final Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        List<Space> spacesWithTag = spaceRepository.findByTags(tag);

        for (Space space : spacesWithTag) {
            space.getTags().remove(tag);
            spaceCacheService.evictAllPagesForTag(tag.getName());
        }

        tagRepository.delete(tag);
    }
}
