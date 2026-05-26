package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for user login")
public record UserLoginRequest(

        @Schema(description = "User's registered email address", example = "user@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @Schema(description = "User's password", example = "strongPassword123")
        @NotBlank(message = "Password is required")
        String password
) { }
