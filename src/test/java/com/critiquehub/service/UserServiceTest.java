package com.critiquehub.service;

import com.critiquehub.dto.UserCreateDto;
import com.critiquehub.dto.UserResponseDto;
import com.critiquehub.mapper.UserMapper;
import com.critiquehub.model.Space;
import com.critiquehub.model.User;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @DisplayName("findAll: success")
    void findAll_Success() {
        User user = new User();
        UserResponseDto dto = new UserResponseDto(1L, "user", "e@m.com");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        List<UserResponseDto> result = userService.findAll();

        assertThat(result).hasSize(1).containsExactly(dto);
    }

    @Test
    @DisplayName("createUser: success")
    void createUser_Success() {
        UserCreateDto createDto = new UserCreateDto("test", "t@m.com", "p");
        User user = new User();
        User saved = new User();
        saved.setUsername("test");
        UserResponseDto resp = new UserResponseDto(1L, "test", "t@m.com");

        when(userRepository.existsByUsername(createDto.username())).thenReturn(false);
        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(resp);

        UserResponseDto result = userService.createUser(createDto);
        assertThat(result).isEqualTo(resp);
    }

    @Test
    @DisplayName("createUser: throw exception when username taken")
    void createUser_Conflict() {
        UserCreateDto dto = new UserCreateDto("taken", "a@b.com", "p");
        when(userRepository.existsByUsername("taken")).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("findById: success")
    void findById_Success() {
        User user = new User();
        UserResponseDto dto = new UserResponseDto(1L, "user", "u@m.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        UserResponseDto result = userService.findById(1L);
        assertThat(result).isEqualTo(dto);
    }

    @Test
    @DisplayName("findById: throw exception when not found")
    void findById_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("updateUser: success with same username")
    void updateUser_SameUsername_Success() {
        Long id = 1L;
        User existing = new User();
        existing.setUsername("name");
        UserCreateDto dto = new UserCreateDto("name", "new@e.com", "p");
        UserResponseDto resp = new UserResponseDto(id, "name", "new@e.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toDto(existing)).thenReturn(resp);

        UserResponseDto result = userService.updateUser(id, dto);
        assertThat(result).isEqualTo(resp);
    }

    @Test
    @DisplayName("updateUser: success with new username")
    void updateUser_NewUsername_Success() {
        Long id = 1L;
        User existing = new User();
        existing.setUsername("old");
        UserCreateDto dto = new UserCreateDto("new", "e@e.com", "p");
        UserResponseDto resp = new UserResponseDto(id, "new", "e@e.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("new")).thenReturn(false);
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toDto(existing)).thenReturn(resp);

        UserResponseDto result = userService.updateUser(id, dto);
        assertThat(result).isEqualTo(resp);
    }

    @Test
    @DisplayName("updateUser: throw exception when new username taken")
    void updateUser_UsernameTaken() {
        User existing = new User();
        existing.setUsername("old");
        UserCreateDto dto = new UserCreateDto("taken", "e@e.com", "p");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("updateUser: throw exception when email blank")
    void updateUser_EmailBlank_ThrowsException() {
        User existing = new User();
        existing.setUsername("name");
        UserCreateDto dto = new UserCreateDto("name", "  ", "p");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("updateUser: throw exception when email null")
    void updateUser_EmailNull_ThrowsException() {
        User existing = new User();
        existing.setUsername("name");
        UserCreateDto dto = new UserCreateDto("name", null, "p");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("deleteUser: success")
    void deleteUser_Success() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteUser: throw exception when not found")
    void deleteUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getUserFavorites: success")
    void getUserFavorites_Success() {
        User user = new User();
        Set<Space> spaces = Set.of(new Space());
        user.setFavoriteSpaces(spaces);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Space> result = userService.getUserFavorites(1L);
        assertThat(result).isEqualTo(spaces);
    }

    @Test
    @DisplayName("addSpaceToFavorites: throw exception when space not found")
    void addSpaceToFavorites_SpaceNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(spaceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.addSpaceToFavorites(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("removeSpaceFromFavorites: success if present")
    void removeSpaceFromFavorites_Success() {
        User user = new User();
        Space space = new Space();
        user.getFavoriteSpaces().add(space);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(2L)).thenReturn(Optional.of(space));

        userService.removeSpaceFromFavorites(1L, 2L);

        assertThat(user.getFavoriteSpaces()).isEmpty();
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    @DisplayName("removeSpaceFromFavorites: user not found")
    void removeSpaceFromFavorites_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.removeSpaceFromFavorites(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void createUser_ShouldThrowException_WhenUsernameExists() {
        UserCreateDto dto = new UserCreateDto("panar0ik", "test@mail.com", "pass");
        when(userRepository.existsByUsername("panar0ik")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already taken");
    }

    @Test
    void addSpaceToFavorites_ShouldWork() {
        User user = new User();
        user.setFavoriteSpaces(new HashSet<>());
        Space space = new Space();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(2L)).thenReturn(Optional.of(space));

        userService.addSpaceToFavorites(1L, 2L);

        assertThat(user.getFavoriteSpaces()).contains(space);
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    void updateUser_ShouldThrow_WhenEmailIsEmpty() {
        User user = new User();
        user.setUsername("old");
        UserCreateDto dto = new UserCreateDto("old", "", "pass"); // Пустой email

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
