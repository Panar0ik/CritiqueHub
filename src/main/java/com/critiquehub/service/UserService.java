package com.critiquehub.service;

import com.critiquehub.dto.UserCreateDto;
import com.critiquehub.dto.UserResponseDto;
import com.critiquehub.mapper.UserMapper;
import com.critiquehub.model.User;
import com.critiquehub.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserResponseDto createUser(final UserCreateDto dto) {
        // Проверка на уникальность username, чтобы не упасть с ошибкой базы
        if (userRepository.existsByUsername(dto.username())) {
            throw new RuntimeException("Username '" + dto.username() + "' is already taken");
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
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public UserResponseDto updateUser(final Long id, final UserCreateDto dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!existingUser.getUsername().equals(dto.username())
                && userRepository.existsByUsername(dto.username())) {
            throw new RuntimeException("Username '" + dto.username() + "' is already taken");
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
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
