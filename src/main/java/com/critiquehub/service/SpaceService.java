package com.critiquehub.service;

import com.critiquehub.dto.SpaceCreateDto;
import com.critiquehub.dto.SpaceResponseDto;
import com.critiquehub.mapper.SpaceMapper;
import com.critiquehub.model.Space;
import com.critiquehub.model.Tag;
import com.critiquehub.model.User;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.TagRepository;
import com.critiquehub.repository.UserRepository;
import com.critiquehub.util.aspect.LogExecutionTime;
import com.critiquehub.util.cache.SpaceCacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final SpaceMapper spaceMapper;
    private final SpaceCacheService spaceCacheService;

    @LogExecutionTime
    @Transactional(readOnly = true)
    public List<SpaceResponseDto> getAllSpaces() {
        return spaceRepository.findAll().stream()
                .map(spaceMapper::toDto)
                .toList();
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public SpaceResponseDto getById(final Long id) {
        return spaceRepository.findById(id)
                .map(spaceMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Space not found with id: " + id));
    }

    @LogExecutionTime
    @Transactional
    public SpaceResponseDto createSpace(final SpaceCreateDto dto) {
        User owner = userRepository.findById(dto.ownerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Space space = new Space();
        space.setName(dto.name());
        space.setDescription(dto.description());
        space.setOwner(owner);
        space.setTags(mapTagNamesToEntities(dto.tagNames()));

        Space saved = spaceRepository.save(space);
        spaceRepository.flush();

        Set<String> tagNames = dto.tagNames();
        registerCacheInvalidation(tagNames);

        return spaceMapper.toDto(saved);
    }

    @LogExecutionTime
    @Transactional
    public SpaceResponseDto updateSpace(final Long id, final SpaceCreateDto dto) {
        Space existingSpace = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Space not found"));

        Set<String> affectedTags = existingSpace.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());
        affectedTags.addAll(dto.tagNames());

        existingSpace.setName(dto.name());
        existingSpace.setDescription(dto.description());
        existingSpace.setTags(mapTagNamesToEntities(dto.tagNames()));

        spaceRepository.saveAndFlush(existingSpace);

        registerCacheInvalidation(affectedTags);

        return spaceMapper.toDto(existingSpace);
    }

    @LogExecutionTime
    @Transactional
    public void deleteSpace(final Long id) {
        Space space = spaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Space not found"));

        Set<String> affectedTags = space.getTags().stream()
                .map(Tag::getName)
                .collect(Collectors.toSet());

        spaceRepository.delete(space);
        spaceRepository.flush();

        registerCacheInvalidation(affectedTags);
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public Page<SpaceResponseDto> getSpacesByTag(final String tagName, final Pageable pageable) {
        return spaceCacheService.getSpacesByTag(tagName, pageable)
                .map(spaceMapper::toDto);
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public void registerCacheInvalidation(final Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }

        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    tags.forEach(spaceCacheService::forceRefreshCacheForTag);
                }
            });
        } else {
            tags.forEach(spaceCacheService::forceRefreshCacheForTag);
        }
    }

    private Set<Tag> mapTagNamesToEntities(final Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        return tagNames.stream()
                .map(name -> {
                    String normalized = name.trim().toLowerCase();
                    return tagRepository.findByName(normalized)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(normalized);
                                return tagRepository.save(newTag);
                            });
                })
                .collect(Collectors.toSet());
    }
}
