package com.critiquehub.service;

import com.critiquehub.dto.UserCreateDto;
import com.critiquehub.dto.UserResponseDto;
import com.critiquehub.mapper.UserMapper;
import com.critiquehub.model.Space;
import com.critiquehub.model.User;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(final UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username '" + dto.username() + "' is already taken"
            );
        }

        User user = userMapper.toEntity(dto);
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDto findById(final Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Transactional
    public UserResponseDto updateUser(final Long id, final UserCreateDto dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        if (!existingUser.getUsername().equals(dto.username())
                && userRepository.existsByUsername(dto.username())) {
            throw new EntityNotFoundException("Username '" + dto.username() + "' is already taken");
        }

        existingUser.setUsername(dto.username());
        existingUser.setEmail(dto.email());
        existingUser.setPassword(dto.password());

        User savedUser = userRepository.save(existingUser);
        return userMapper.toDto(savedUser);
    }

    @Transactional
    public void deleteUser(final Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Set<Space> getUserFavorites(final Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return user.getFavoriteSpaces();
    }

    @Transactional
    public void addSpaceToFavorites(final Long userId, final Long spaceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new EntityNotFoundException("Space not found"));

        user.getFavoriteSpaces().add(space);
    }

    @Transactional
    public void removeSpaceFromFavorites(final Long userId, final Long spaceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new EntityNotFoundException("Пространство не найдено"));

        user.getFavoriteSpaces().remove(space);
        userRepository.save(user);
    }
}
