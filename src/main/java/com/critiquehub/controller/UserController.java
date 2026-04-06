package com.critiquehub.controller;

import com.critiquehub.dto.SpaceDto.SpaceResponseDto;
import com.critiquehub.dto.UserDto.UserCreateDto;
import com.critiquehub.dto.UserDto.UserResponseDto;
import com.critiquehub.mapper.SpaceMapper;
import com.critiquehub.service.UserService;
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
public class UserController {

    private final UserService userService;
    private final SpaceMapper spaceMapper;

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(final @RequestBody UserCreateDto dto) {
        return new ResponseEntity<>(userService.createUser(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(final @PathVariable Long id) {
        return userService.findById(id);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(final @PathVariable Long id, final @RequestBody UserCreateDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(final @PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{userId}/favorites/{spaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFavorite(final @PathVariable Long userId, final @PathVariable Long spaceId) {
        userService.addSpaceToFavorites(userId, spaceId);
    }

    @GetMapping("/{userId}/favorites")
    public List<SpaceResponseDto> getFavorites(final @PathVariable Long userId) {
        return userService.getUserFavorites(userId).stream()
                .map(spaceMapper::toDto)
                .toList();
    }

    @DeleteMapping("/{userId}/favorites/{spaceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(final @PathVariable Long userId, final @PathVariable Long spaceId) {
        userService.removeSpaceFromFavorites(userId, spaceId);
    }
}
