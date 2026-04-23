package com.critiquehub.util.cache;

public record SpaceCacheKey(
        String tagName,
        int pageNumber,
        int pageSize
) {
    public SpaceCacheKey {
            tagName = (tagName == null) ? "" : tagName.toLowerCase().trim();
    }
}
