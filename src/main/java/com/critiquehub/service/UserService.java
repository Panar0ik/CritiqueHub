package com.critiquehub.service;

import com.critiquehub.dto.UserCreateDto;
import com.critiquehub.dto.UserResponseDto;
import com.critiquehub.mapper.UserMapper;
import com.critiquehub.model.Space;
import com.critiquehub.model.User;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.UserRepository;
import com.critiquehub.util.aspect.LogExecutionTime;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final String USER_NOT_FOUND = "User not found";

    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;
    private final UserMapper userMapper;

    @LogExecutionTime
    @Transactional
    public UserResponseDto createUser(final UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalStateException("Username '" + dto.username() + "' is already taken");
        }

        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        log.info("Created new user with username: {}", saved.getUsername());
        return userMapper.toDto(saved);
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public UserResponseDto findById(final Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @LogExecutionTime
    @Transactional
    public UserResponseDto updateUser(final Long id, final UserCreateDto dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (!existingUser.getUsername().equals(dto.username())
                && userRepository.existsByUsername(dto.username())) {
            throw new IllegalStateException("Username '" + dto.username() + "' is already taken");
        }

        if (dto.email() == null || dto.email().isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        existingUser.setUsername(dto.username());
        existingUser.setEmail(dto.email());
        existingUser.setPassword(dto.password());

        return userMapper.toDto(userRepository.save(existingUser));
    }

    @LogExecutionTime
    @Transactional
    public void deleteUser(final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        userRepository.delete(user);
        log.info("Deleted user with id: {}", id);
    }

    @LogExecutionTime
    @Transactional(readOnly = true)
    public Set<Space> getUserFavorites(final Long userId) {
        return userRepository.findById(userId)
                .map(User::getFavoriteSpaces)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    @LogExecutionTime
    @Transactional
    public void addSpaceToFavorites(final Long userId, final Long spaceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new EntityNotFoundException("Space not found"));

        user.getFavoriteSpaces().add(space);
        userRepository.saveAndFlush(user);
    }

    @LogExecutionTime
    @Transactional
    public void removeSpaceFromFavorites(final Long userId, final Long spaceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new EntityNotFoundException("Space not found"));

        if (user.getFavoriteSpaces().remove(space)) {
            userRepository.saveAndFlush(user);
        }
    }
}
