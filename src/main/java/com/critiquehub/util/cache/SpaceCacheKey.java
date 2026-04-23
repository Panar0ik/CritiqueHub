package com.critiquehub.service.cache;

public record SpaceCacheKey(
        String tagName,
        int pageNumber,
        int pageSize
) {
    public SpaceCacheKey {
        if (tagName == null) {
            tagName = ""; // Or handle as needed
        }
    }
}
