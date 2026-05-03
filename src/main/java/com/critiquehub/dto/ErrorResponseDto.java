package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Global error response format")
public record ErrorResponseDto(
        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Error type", example = "Bad Request")
        String error,

        @Schema(description = "Detailed error message", example = "Validation failed")
        String message,

        @Schema(description = "Timestamp of the error", example = "1672531200000")
        long timestamp,

        @Schema(description = "List of specific error details")
        List<String> details
) { }
