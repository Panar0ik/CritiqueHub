package com.critiquehub.util.cache;

import com.critiquehub.model.Space;
import com.critiquehub.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceCacheService {

    private final SpaceRepository spaceRepository;

    private final Map<SpaceCacheKey, Page<Space>> cache = new ConcurrentHashMap<>();

    public Page<Space> getSpacesByTag(final String tagName, final Pageable pageable) {
        SpaceCacheKey key = new SpaceCacheKey(tagName, pageable.getPageNumber(), pageable.getPageSize());

        return cache.computeIfAbsent(key, k -> {
            log.info("Cache miss. Fetching from database for tag: {}", tagName);
            return spaceRepository.findByTagNameJPQL(tagName, pageable);
        });
    }

    public void evictAllPagesForTag(final String tagName) {
        if (tagName == null) {
            return;
        }
        log.info("Clear cache for tags: {}", tagName);
        cache.keySet().removeIf(key -> tagName.equals(key.tagName()));
    }
}
