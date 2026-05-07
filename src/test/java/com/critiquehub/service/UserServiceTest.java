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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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
    @DisplayName("createUser: conflict")
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
    @DisplayName("findById: not found")
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
    @DisplayName("updateUser: username taken")
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
    @DisplayName("updateUser: success when username remains the same")
    void updateUser_UsernameUnchanged_Success() {
        Long id = 1L;
        User existing = new User();
        existing.setUsername("panar0ik");

        UserCreateDto dto = new UserCreateDto("panar0ik", "new@email.com", "password");
        UserResponseDto resp = new UserResponseDto(id, "panar0ik", "new@email.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(existing);
        when(userMapper.toDto(any(User.class))).thenReturn(resp);

        UserResponseDto result = userService.updateUser(id, dto);

        verify(userRepository, never()).existsByUsername(anyString());
        assertThat(result.username()).isEqualTo("panar0ik");
    }

    @Test
    @DisplayName("addSpaceToFavorites: throw exception when Space not found")
    void addSpaceToFavorites_SpaceNotFound_Throws() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(spaceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.addSpaceToFavorites(1L, 2L));
    }

    @Test
    @DisplayName("removeSpaceFromFavorites: do nothing if space is not in favorites")
    void removeSpaceFromFavorites_NotInFavorites_DoesNotSave() {
        User user = new User();
        user.setFavoriteSpaces(new HashSet<>()); // Пустой список
        Space space = new Space();
        space.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(2L)).thenReturn(Optional.of(space));

        userService.removeSpaceFromFavorites(1L, 2L);

        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("updateUser: user not found")
    void updateUser_UserNotFound_ThrowsException() {
        UserCreateDto dto = new UserCreateDto("new", "new@test.com", "pass");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    @DisplayName("updateUser: email blank")
    void updateUser_EmailBlank_ThrowsException() {
        User existing = new User();
        existing.setUsername("name");
        UserCreateDto dto = new UserCreateDto("name", "  ", "p");
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
    @DisplayName("deleteUser: not found")
    void deleteUser_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getUserFavorites: success")
    void getUserFavorites_Success() {
        User user = new User();
        Set<Space> spaces = new HashSet<>(List.of(new Space()));
        user.setFavoriteSpaces(spaces);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Set<Space> result = userService.getUserFavorites(1L);
        assertThat(result).isEqualTo(spaces);
    }

    @Test
    @DisplayName("getUserFavorites: user not found")
    void getUserFavorites_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserFavorites(1L));
    }

    @Test
    @DisplayName("addSpaceToFavorites: success")
    void addSpaceToFavorites_Success() {
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
    @DisplayName("addSpaceToFavorites: space not found")
    void addSpaceToFavorites_SpaceNotFound_ThrowsException() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.addSpaceToFavorites(1L, 2L));
    }

    @Test
    @DisplayName("removeSpaceFromFavorites: success if present")
    void removeSpaceFromFavorites_Success() {
        User user = new User();
        Space space = new Space();
        space.setId(2L);
        Set<Space> favorites = new HashSet<>();
        favorites.add(space);
        user.setFavoriteSpaces(favorites);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(2L)).thenReturn(Optional.of(space));

        userService.removeSpaceFromFavorites(1L, 2L);

        assertTrue(user.getFavoriteSpaces().isEmpty());
        verify(userRepository).saveAndFlush(user);
    }

    @Test
    @DisplayName("removeSpaceFromFavorites: space not in favorites")
    void removeSpaceFromFavorites_NotPresent_ShouldNotSave() {
        User user = new User();
        user.setFavoriteSpaces(new HashSet<>());
        Space space = new Space();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(2L)).thenReturn(Optional.of(space));

        userService.removeSpaceFromFavorites(1L, 2L);

        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("removeSpaceFromFavorites: space not found")
    void removeSpaceFromFavorites_SpaceNotFound_ThrowsException() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.removeSpaceFromFavorites(1L, 2L));
    }

    @Test
    @DisplayName("removeSpaceFromFavorites: user not found")
    void removeSpaceFromFavorites_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.removeSpaceFromFavorites(1L, 2L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("updateUser: выброс исключения при поиске пользователя")
    void updateUser_UserNotFound_LambdaCoverage() {
        Long userId = 1L;
        UserCreateDto dto = new UserCreateDto("new", "e@e.com", "p");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.updateUser(userId, dto)
        );
    }

    @Test
    @DisplayName("addSpaceToFavorites: выброс исключения при поиске Space")
    void addSpaceToFavorites_SpaceNotFound_LambdaCoverage() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(spaceRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.addSpaceToFavorites(1L, 2L));
    }

    @Test
    @DisplayName("getUserFavorites: выброс исключения при поиске пользователя")
    void getUserFavorites_UserNotFound_LambdaCoverage() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserFavorites(1L));
    }

    @Test
    @DisplayName("removeSpaceFromFavorites: если объекта нет в списке, сохранение не вызывается")
    void removeSpaceFromFavorites_NotPresent_NoSave() {
        User user = new User();
        user.setFavoriteSpaces(new HashSet<>()); // Пусто
        Space space = new Space();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(spaceRepository.findById(anyLong())).thenReturn(Optional.of(space));

        userService.removeSpaceFromFavorites(1L, 2L);
        verify(userRepository, never()).saveAndFlush(any());
    }

    @Test
    void addSpaceToFavorites_ShouldThrowException_WhenUserNotFound() {
        Long userId = 999L;
        Long spaceId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                userService.addSpaceToFavorites(userId, spaceId)
        );

        assertTrue(exception.getMessage().contains("not found") || exception.getMessage().equals("User not found"));

        verify(spaceRepository, never()).findById(any());
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailIsNull() {
        Long userId = 1L;
        String sameUsername = "testUser";

        UserCreateDto dto = new UserCreateDto(sameUsername, null, "password");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername(sameUsername);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, dto)
        );
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailIsBlank() {
        Long userId = 1L;
        String sameUsername = "testUser";

        UserCreateDto dto = new UserCreateDto(sameUsername, "   ", "password");

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername(sameUsername);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () ->
                userService.updateUser(userId, dto)
        );
    }
}
