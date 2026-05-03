package com.critiquehub.controller;

import com.critiquehub.dto.SpaceResponseDto;
import com.critiquehub.dto.UserCreateDto;
import com.critiquehub.dto.UserResponseDto;
import com.critiquehub.mapper.SpaceMapper;
import com.critiquehub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management and favorites APIs")
public class UserController {

    private final UserService userService;
    private final SpaceMapper spaceMapper;

    @PostMapping
    @Operation(summary = "Register a new user")
    public ResponseEntity<UserResponseDto> createUser(final @Valid @RequestBody UserCreateDto dto) {
        return new ResponseEntity<>(userService.createUser(dto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all users")
    public List<UserResponseDto> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public UserResponseDto getById(final @PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public UserResponseDto updateUser(final @PathVariable Long id, final @RequestBody UserCreateDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete user")
    public void delete(final @PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{userId}/favorites/{spaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Add space to user favorites")
    public void addFavorite(final @PathVariable Long userId, final @PathVariable Long spaceId) {
        userService.addSpaceToFavorites(userId, spaceId);
    }

    @GetMapping("/{userId}/favorites")
    @Operation(summary = "Get user's favorite spaces")
    public List<SpaceResponseDto> getFavorites(final @PathVariable Long userId) {
        return userService.getUserFavorites(userId).stream()
                .map(spaceMapper::toDto)
                .toList();
    }

    @DeleteMapping("/{userId}/favorites/{spaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove space from user favorites")
    public void removeFavorite(final @PathVariable Long userId, final @PathVariable Long spaceId) {
        userService.removeSpaceFromFavorites(userId, spaceId);
    }
}
