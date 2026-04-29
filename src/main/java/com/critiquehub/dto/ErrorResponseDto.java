package com.critiquehub.dto;

import java.util.List;

public record ErrorResponseDto(
        int status,
        String error,
        String message,
        long timestamp,
        List<String> details
) { }
