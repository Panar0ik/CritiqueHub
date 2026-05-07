package com.critiquehub.service;

import com.critiquehub.dto.SpaceCreateDto;
import com.critiquehub.dto.SpaceResponseDto;
import com.critiquehub.mapper.SpaceMapper;
import com.critiquehub.model.Space;
import com.critiquehub.model.Tag;
import com.critiquehub.model.User;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.TagRepository;
import com.critiquehub.repository.UserRepository;
import com.critiquehub.util.cache.SpaceCacheService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpaceServiceTest {

    @Mock private SpaceRepository spaceRepository;
    @Mock private UserRepository userRepository;
    @Mock private TagRepository tagRepository;
    @Mock private SpaceMapper spaceMapper;
    @Mock private SpaceCacheService spaceCacheService;

    @InjectMocks
    private SpaceService spaceService;

    @Test
    void getAllSpaces_Success() {
        Space space = new Space();
        SpaceResponseDto dto = new SpaceResponseDto(1L, "Name", "Desc", "Owner", Set.of());

        when(spaceRepository.findAll()).thenReturn(List.of(space));
        when(spaceMapper.toDto(space)).thenReturn(dto);

        List<SpaceResponseDto> result = spaceService.getAllSpaces();

        assertEquals(1, result.size());
        verify(spaceRepository).findAll();
    }

    @Test
    void getById_Success() {
        Long id = 1L;
        Space space = new Space();
        SpaceResponseDto dto = new SpaceResponseDto(id, "Name", "Desc", "Owner", Set.of());

        when(spaceRepository.findById(id)).thenReturn(Optional.of(space));
        when(spaceMapper.toDto(space)).thenReturn(dto);

        SpaceResponseDto result = spaceService.getById(id);

        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void getById_NotFound_ThrowsException() {
        when(spaceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> spaceService.getById(1L));
    }

    @Test
    void createSpace_Success() {
        SpaceCreateDto dto = new SpaceCreateDto("New Space", "Desc", 1L, Set.of(" Java ", "Spring"));
        User owner = new User();

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(tagRepository.findByName("java")).thenReturn(Optional.empty());
        when(tagRepository.findByName("spring")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(i -> i.getArguments()[0]);
        when(spaceRepository.save(any(Space.class))).thenAnswer(i -> i.getArguments()[0]);
        when(spaceMapper.toDto(any())).thenReturn(new SpaceResponseDto(1L, "New Space", "Desc", "Owner", Set.of("java", "spring")));

        SpaceResponseDto result = spaceService.createSpace(dto);

        assertNotNull(result);
        verify(tagRepository).findByName("java");
        verify(tagRepository).findByName("spring");
        verify(spaceRepository).save(any());
        verify(spaceCacheService, atLeastOnce()).forceRefreshCacheForTag(anyString());
    }

    @Test
    void createSpace_UserNotFound_ThrowsException() {
        SpaceCreateDto dto = new SpaceCreateDto("Name", "Desc", 1L, Set.of());
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> spaceService.createSpace(dto));
    }

    @Test
    void updateSpace_Success() {
        Long id = 1L;
        Tag oldTag = new Tag();
        oldTag.setName("old");
        Space existingSpace = new Space();
        existingSpace.setTags(new HashSet<>(Set.of(oldTag)));

        SpaceCreateDto dto = new SpaceCreateDto("Updated", "Desc", 1L, Set.of("new"));

        when(spaceRepository.findById(id)).thenReturn(Optional.of(existingSpace));
        when(tagRepository.findByName("new")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(i -> i.getArguments()[0]);
        when(spaceMapper.toDto(any())).thenReturn(new SpaceResponseDto(id, "Updated", "Desc", "Owner", Set.of("new")));

        SpaceResponseDto result = spaceService.updateSpace(id, dto);

        assertEquals("Updated", result.name());
        verify(spaceRepository).saveAndFlush(existingSpace);
        verify(spaceCacheService).forceRefreshCacheForTag("old");
        verify(spaceCacheService).forceRefreshCacheForTag("new");
    }

    @Test
    void updateSpace_NotFound_ThrowsException() {
        when(spaceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> spaceService.updateSpace(1L, mock(SpaceCreateDto.class)));
    }

    @Test
    void deleteSpace_Success() {
        Long id = 1L;
        Space space = new Space();
        Tag tag = new Tag();
        tag.setName("tech");
        space.setTags(Set.of(tag));

        when(spaceRepository.findById(id)).thenReturn(Optional.of(space));

        spaceService.deleteSpace(id);

        verify(spaceRepository).delete(space);
        verify(spaceCacheService).forceRefreshCacheForTag("tech");
    }

    @Test
    void deleteSpace_NotFound_ThrowsException() {
        when(spaceRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> spaceService.deleteSpace(1L));
    }

    @Test
    void getSpacesByTag_Success() {
        String tag = "java";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Space> page = new PageImpl<>(List.of(new Space()));

        when(spaceCacheService.getSpacesByTag(tag, pageable)).thenReturn(page);
        when(spaceMapper.toDto(any())).thenReturn(mock(SpaceResponseDto.class));

        Page<SpaceResponseDto> result = spaceService.getSpacesByTag(tag, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void mapTagNamesToEntities_Null_ReturnsEmptySet() {
        SpaceCreateDto dto = new SpaceCreateDto("Name", "Desc", 1L, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(spaceRepository.save(any())).thenReturn(new Space());

        spaceService.createSpace(dto);

        verify(tagRepository, never()).findByName(any());
    }

    @Test
    void createSpace_ShouldNormalizeTagNames() {
        SpaceCreateDto dto = new SpaceCreateDto("Name", "Desc", 1L, Set.of("  Java  ", "Spring  "));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(tagRepository.findByName(anyString())).thenReturn(Optional.of(new Tag()));
        when(spaceRepository.save(any())).thenReturn(new Space());

        spaceService.createSpace(dto);

        verify(tagRepository).findByName("java");
        verify(tagRepository).findByName("spring");
    }

    @Test
    void registerCacheInvalidation_ShouldHandleActiveTransaction() {
        Long spaceId = 1L;
        Space space = new Space();
        Tag tag = new Tag();
        tag.setName("java");
        space.setTags(Set.of(tag));

        try (var mockedStatic = mockStatic(TransactionSynchronizationManager.class)) {
            mockedStatic.when(TransactionSynchronizationManager::isActualTransactionActive)
                    .thenReturn(true);

            when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

            spaceService.deleteSpace(spaceId);

            mockedStatic.verify(() ->
                    TransactionSynchronizationManager.registerSynchronization(any(TransactionSynchronization.class))
            );

            ArgumentCaptor<TransactionSynchronization> syncCaptor = ArgumentCaptor.forClass(TransactionSynchronization.class);
            mockedStatic.verify(() -> TransactionSynchronizationManager.registerSynchronization(syncCaptor.capture()));

            TransactionSynchronization synchronization = syncCaptor.getValue();
            synchronization.afterCommit();

            verify(spaceCacheService).forceRefreshCacheForTag("java");
        }
    }

    @Test
    void mapTagNamesToEntities_Empty_ReturnsEmptySet() {
        SpaceCreateDto dto = new SpaceCreateDto("Name", "Desc", 1L, Collections.emptySet());

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(spaceRepository.save(any())).thenReturn(new Space());

        spaceService.createSpace(dto);

        verify(tagRepository, never()).findByName(any());
    }
}
