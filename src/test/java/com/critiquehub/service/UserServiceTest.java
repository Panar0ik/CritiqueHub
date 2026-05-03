package com.critiquehub.service;

import com.critiquehub.dto.UserCreateDto;
import com.critiquehub.dto.UserResponseDto;
import com.critiquehub.mapper.UserMapper;
import com.critiquehub.model.Space;
import com.critiquehub.model.User;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_Success() {
        UserCreateDto dto = new UserCreateDto("john", "john@mail.com", "pass");
        User user = new User();
        UserResponseDto expected = new UserResponseDto(1L, "john", "john@mail.com");

        when(userRepository.existsByUsername(dto.username())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponseDto result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("john", result.username());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_Conflict_ThrowsException() {
        UserCreateDto dto = new UserCreateDto("exists", "e@mail.com", "pass");
        when(userRepository.existsByUsername("exists")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.createUser(dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void findAll_Success() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));
        when(userMapper.toDto(any())).thenReturn(new UserResponseDto(1L, "u", "e"));

        List<UserResponseDto> result = userService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findById_Success() {
        Long id = 1L;
        User user = new User();
        UserResponseDto dto = new UserResponseDto(id, "u", "e");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserResponseDto result = userService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void findById_NotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void updateUser_Success() {
        Long id = 1L;
        UserCreateDto dto = new UserCreateDto("newNick", "new@mail.com", "newPass");
        User existingUser = new User();
        existingUser.setUsername("oldNick");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("newNick")).thenReturn(false);
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDto(existingUser)).thenReturn(new UserResponseDto(id, "newNick", "new@mail.com"));

        UserResponseDto result = userService.updateUser(id, dto);

        assertEquals("newNick", result.username());
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_UsernameTaken_ThrowsException() {
        Long id = 1L;
        UserCreateDto dto = new UserCreateDto("taken", "e@mail.com", "p");
        User existingUser = new User();
        existingUser.setUsername("myNick");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(id, dto));
    }

    @Test
    void deleteUser_Success() {
        Long id = 1L;
        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void getUserFavorites_Success() {
        Long userId = 1L;
        User user = new User();
        Set<Space> spaces = new HashSet<>();
        user.setFavoriteSpaces(spaces);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Set<Space> result = userService.getUserFavorites(userId);

        assertEquals(spaces, result);
    }

    @Test
    void addSpaceToFavorites_Success() {
        Long userId = 1L;
        Long spaceId = 2L;
        User user = new User();
        user.setFavoriteSpaces(new HashSet<>());
        Space space = new Space();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        userService.addSpaceToFavorites(userId, spaceId);

        assertTrue(user.getFavoriteSpaces().contains(space));
    }

    @Test
    void removeSpaceFromFavorites_Success() {
        Long userId = 1L;
        Long spaceId = 2L;
        User user = new User();
        Space space = new Space();
        Set<Space> favorites = new HashSet<>();
        favorites.add(space);
        user.setFavoriteSpaces(favorites);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        userService.removeSpaceFromFavorites(userId, spaceId);

        assertTrue(user.getFavoriteSpaces().isEmpty());
        verify(userRepository).save(user);
    }

    @Test
    void removeSpaceFromFavorites_SpaceNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(spaceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.removeSpaceFromFavorites(1L, 2L));
    }
}
