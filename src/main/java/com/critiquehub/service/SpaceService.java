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
import com.critiquehub.util.cache.SpaceCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final SpaceMapper spaceMapper;
    private final SpaceCacheService spaceCacheService;

    @Transactional
    public SpaceResponseDto createSpace(final SpaceCreateDto dto) {
        User owner = userRepository.findById(dto.ownerId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Space space = new Space();
        space.setName(dto.name());
        space.setDescription(dto.description());
        space.setOwner(owner);
        space.setTags(mapTagNamesToEntities(dto.tagNames()));

        SpaceResponseDto saved = spaceMapper.toDto(spaceRepository.save(space));
        spaceCacheService.evictCache();
        return saved;
    }

    @Transactional(readOnly = true)
    public List<SpaceResponseDto> getAllSpaces() {
        return spaceRepository.findAll().stream()
                .map(spaceMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpaceResponseDto getById(final Long id) {
        return spaceRepository.findById(id)
                .map(spaceMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Space not found with id: " + id));
    }

    @Transactional
    public SpaceResponseDto updateSpace(final Long id, final SpaceCreateDto dto) {
        Space existingSpace = spaceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Space not found"));

        existingSpace.setName(dto.name());
        existingSpace.setDescription(dto.description());
        existingSpace.setTags(mapTagNamesToEntities(dto.tagNames()));

        SpaceResponseDto updated = spaceMapper.toDto(spaceRepository.save(existingSpace));
        spaceCacheService.evictCache();
        return updated;
    }

    @Transactional
    public void deleteSpace(final Long id) {
        if (!spaceRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete: Space not found");
        }
        spaceRepository.deleteById(id);
        spaceCacheService.evictCache();
    }

    @Transactional(readOnly = true)
    public Page<SpaceResponseDto> getSpacesByTag(final String tagName, final Pageable pageable) {
        return spaceCacheService.getSpacesByTag(tagName, pageable)
                .map(spaceMapper::toDto);
    }

    private Set<Tag> mapTagNamesToEntities(final Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        return tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(name);
                            return tagRepository.save(newTag);
                        }))
                .collect(Collectors.toSet());
    }
}
