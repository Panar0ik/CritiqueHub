package com.critiquehub.service;

import com.critiquehub.dto.TagCreateDto;
import com.critiquehub.dto.TagDto;
import com.critiquehub.model.Space;
import com.critiquehub.model.Tag;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.TagRepository;
import com.critiquehub.util.aspect.LogExecutionTime;
import com.critiquehub.util.async.ApplyAsync;
import com.critiquehub.util.async.MetricsCounter;
import com.critiquehub.util.cache.SpaceCacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final TagRepository tagRepository;
    private final SpaceRepository spaceRepository;
    private final SpaceCacheService spaceCacheService;
    private final MetricsCounter metricsCounter;

    @LogExecutionTime
    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public Tag getByName(final String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + name));
    }

    @LogExecutionTime
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

    @LogExecutionTime
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

    @LogExecutionTime
    @Transactional
    public void deleteTag(final Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found"));

        String tagName = tag.getName();
        List<Space> spacesWithTag = spaceRepository.findByTags(tag);

        for (Space space : spacesWithTag) {
            space.getTags().remove(tag);
        }

        spaceRepository.saveAllAndFlush(spacesWithTag);
        tagRepository.delete(tag);
        tagRepository.flush();

        spaceCacheService.forceRefreshCacheForTag(tagName);
    }

    @ApplyAsync("BULK_CREATE_TAGS")
    @LogExecutionTime
    @Transactional
    public String createTagsBulkRaw(final Long spaceId, final List<TagCreateDto> dtos) {

        final Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new EntityNotFoundException("Space not found with id: " + spaceId));

        log.info("[Bulk Task] Starting tag mapping. Current metrics global count: {}",
                metricsCounter.getAtomicValue());

        final List<Tag> tagsToSave = dtos.stream()
                .map(dto -> {
                    final Tag tag = tagRepository.findByName(dto.name()).orElseGet(() -> {
                        final Tag newTag = new Tag();
                        newTag.setName(dto.name());
                        newTag.setSpaces(new java.util.HashSet<>());
                        return newTag;
                    });

                    if (tag.getSpaces() == null) {
                        tag.setSpaces(new java.util.HashSet<>());
                    }

                    if (space.getTags() == null) {
                        space.setTags(new java.util.HashSet<>());
                    }

                    tag.getSpaces().add(space);
                    space.getTags().add(tag);

                    final int currentCount = metricsCounter.incrementAtomic();
                    log.debug("[Metrics] Processed tag: {}. Global metrics counter: {}", dto.name(), currentCount);

                    return tag;
                })
                .toList();

        final long debugSleepMillis = 5000L;
        try {
            Thread.sleep(debugSleepMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("The background waiting thread was interrupted", e);
        }

        try {
            tagRepository.saveAll(tagsToSave);
            spaceRepository.save(space);

            log.info("[Bulk Task] Successfully saved {} tags and linked to space {}", tagsToSave.size(), spaceId);
        } catch (Exception e) {
            log.error("[Bulk Task] FATAL ERROR during DB save: ", e);
        }

        return null;
    }
}
