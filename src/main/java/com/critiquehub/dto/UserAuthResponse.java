package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object containing authentication token and user details")
public record UserAuthResponse(
        @Schema(description = "JWT Access Token used for subsequent requests", example = "eyJhbGciOiJIUzI1...")
        String token,

        @Schema(description = "Basic information about the authenticated user")
        UserResponseDto user
) { }
