package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing public user information")
public record UserResponseDto(
        @Schema(description = "User unique ID", example = "1")
        Long id,

        @Schema(description = "Username", example = "john_doe")
        String username,

        @Schema(description = "User email", example = "john@example.com")
        String email
) { }
