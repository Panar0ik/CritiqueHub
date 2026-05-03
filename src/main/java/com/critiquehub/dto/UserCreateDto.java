package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request for user registration")
public record UserCreateDto(
        @NotBlank(message = "Username is required")
        @Size(min = MIN_USERNAME_LENGTH, max = MAX_USERNAME_LENGTH, message = "Username length error")
        @Schema(description = "Unique username", example = "john_doe")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Schema(description = "User email address", example = "john@example.com")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = MIN_PASSWORD_LENGTH, message = "Password too short")
        @Schema(description = "User password (minimum 8 characters)", example = "password123")
        String password
) {
    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MAX_USERNAME_LENGTH = 50;
    public static final int MIN_PASSWORD_LENGTH = 8;
}
