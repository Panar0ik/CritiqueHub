package com.critiquehub.util.cache;

import com.critiquehub.model.Space;
import com.critiquehub.repository.SpaceRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceCacheService {
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final SpaceRepository spaceRepository;
    private final EntityManager entityManager;
    private final Map<SpaceCacheKey, Page<Space>> cache = new ConcurrentHashMap<>();

    public Page<Space> getSpacesByTag(final String tagName, final Pageable pageable) {
        SpaceCacheKey key = new SpaceCacheKey(tagName, pageable.getPageNumber(), pageable.getPageSize());
        Page<Space> cached = cache.get(key);
        if (cached != null) {
            log.info("Cache HIT for tag: {}", tagName);
            return cached;
        }

        log.info("Cache MISS for tag: {}. Fetching from DB...", tagName);
        Page<Space> fresh = spaceRepository.findByTagNameJPQL(tagName, pageable);
        cache.put(key, fresh);
        return fresh;
    }

    public void forceRefreshCacheForTag(final String tagName) {
        if (tagName == null) {
            return;
        }

        entityManager.clear();

        boolean wasInCache = false;
        for (SpaceCacheKey key : cache.keySet()) {
            if (key.tagName().equalsIgnoreCase(tagName)) {
                refreshKey(key);
                wasInCache = true;
            }
        }

        if (!wasInCache) {
            refreshKey(new SpaceCacheKey(tagName, 0, DEFAULT_PAGE_SIZE));
        }
    }

    private void refreshKey(final SpaceCacheKey key) {
        Pageable pageable = PageRequest.of(key.pageNumber(), key.pageSize());
        Page<Space> freshPage = spaceRepository.findByTagNameJPQL(key.tagName(), pageable);
        cache.put(key, freshPage);
        log.info("Cache proactively updated for tag: {}", key.tagName());
    }

    public void evictAllPagesForTag(final String tagName) {
        if (tagName == null) {
            return;
        }
        cache.keySet().removeIf(key -> key.tagName().equalsIgnoreCase(tagName));
    }
}
