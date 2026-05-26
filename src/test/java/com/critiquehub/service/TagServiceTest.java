package com.critiquehub.service;

import com.critiquehub.dto.SpaceResponseDto;
import com.critiquehub.dto.TagCreateDto;
import com.critiquehub.dto.TagDto;
import com.critiquehub.mapper.SpaceMapper;
import com.critiquehub.model.Space;
import com.critiquehub.model.Tag;
import com.critiquehub.model.User;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.TagRepository;
import com.critiquehub.util.cache.SpaceCacheService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private SpaceCacheService spaceCacheService;

    @Mock
    private SpaceMapper spaceMapper;

    @InjectMocks
    private TagService tagService;

    @Test
    void getAllTags_Success() {
        when(tagRepository.findAll()).thenReturn(List.of(new Tag(), new Tag()));

        List<Tag> result = tagService.getAllTags();

        assertEquals(2, result.size());
        verify(tagRepository).findAll();
    }

    @Test
    void getByName_Success() {
        String name = "java";
        Tag tag = new Tag();
        tag.setName(name);
        when(tagRepository.findByName(name)).thenReturn(Optional.of(tag));

        Tag result = tagService.getByName(name);

        assertNotNull(result);
        assertEquals(name, result.getName());
    }

    @Test
    void getByName_NotFound_ThrowsException() {
        when(tagRepository.findByName("unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.getByName("unknown"));
    }

    @Test
    void getByName_ShouldReturnTag_WhenExists() {
        Tag tag = new Tag();
        tag.setName("Anime");
        when(tagRepository.findByName("Anime")).thenReturn(Optional.of(tag));

        Tag result = tagService.getByName("Anime");

        assertThat(result.getName()).isEqualTo("Anime");
    }

    @Test
    void getByName_ShouldThrowException_WhenNotFound() {
        when(tagRepository.findByName("Unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tagService.getByName("Unknown"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateTagName_ShouldThrow_WhenNameAlreadyTakenByAnotherId() {
        Tag existingTag = new Tag();
        existingTag.setId(1L);
        Tag anotherTag = new Tag();
        anotherTag.setId(2L);

        when(tagRepository.findById(1L)).thenReturn(Optional.of(existingTag));
        when(tagRepository.findByName("NewName")).thenReturn(Optional.of(anotherTag));

        assertThatThrownBy(() -> tagService.updateTagName(1L, "NewName"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void createTagsBulkRaw_ShouldWorkCorrectly() {
        Long spaceId = 1L;
        Space space = new Space();
        space.setId(spaceId);
        space.setTags(new HashSet<>());

        TagCreateDto dto = new TagCreateDto("NewTag");
        Tag savedTag = new Tag();
        savedTag.setId(10L);
        savedTag.setName("NewTag");
        savedTag.setSpaces(new HashSet<>());

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(tagRepository.findByName("NewTag")).thenReturn(Optional.empty());
        when(tagRepository.saveAll(any())).thenReturn(List.of(savedTag));

        tagService.createTagsBulkRaw(spaceId, List.of(dto));

        verify(tagRepository).findByName("NewTag");
        verify(tagRepository).saveAll(any());
        verify(tagRepository).flush();
        verify(spaceRepository).findById(spaceId);
        verify(spaceRepository).save(space);
    }

    @Test
    void saveTag_AlreadyExists_ReturnsExisting() {
        String name = "spring";
        Tag existingTag = new Tag();
        existingTag.setName(name);
        TagDto dto = new TagDto(null, name, List.of());

        when(tagRepository.findByName(name)).thenReturn(Optional.of(existingTag));

        Tag result = tagService.saveTag(dto);

        assertEquals(existingTag, result);
        verify(tagRepository, never()).save(any());
    }

    @Test
    void saveTag_NewTag_SavesAndReturns() {
        String name = "new-tag";
        TagDto dto = new TagDto(null, name, List.of());
        when(tagRepository.findByName(name)).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tag result = tagService.saveTag(dto);

        assertEquals(name, result.getName());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void updateTagName_Success() {
        Long id = 1L;
        String newName = "updated";
        Tag tag = new Tag();
        tag.setId(id);

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        when(tagRepository.findByName(newName)).thenReturn(Optional.empty());
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag result = tagService.updateTagName(id, newName);

        assertEquals(newName, result.getName());
    }

    @Test
    void updateTagName_NameAlreadyTaken_ThrowsException() {
        Long id = 1L;
        String newName = "taken";
        Tag tag = new Tag();
        tag.setId(id);

        Tag existingOther = new Tag();
        existingOther.setId(2L);

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        when(tagRepository.findByName(newName)).thenReturn(Optional.of(existingOther));

        assertThrows(EntityNotFoundException.class, () -> tagService.updateTagName(id, newName));
    }

    @Test
    void updateTagName_SameTagAlreadyHasThisName_DoesNotThrow() {
        Long id = 1L;
        String name = "same";
        Tag tag = new Tag();
        tag.setId(id);

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        when(tagRepository.findByName(name)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(tag);

        Tag result = tagService.updateTagName(id, name);

        assertEquals(name, result.getName());
    }

    @Test
    void deleteTag_Success() {
        Long id = 1L;
        String tagName = "toDelete";
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(tagName);

        Space space = new Space();
        Set<Tag> tags = new HashSet<>();
        tags.add(tag);
        space.setTags(tags);

        when(tagRepository.findById(id)).thenReturn(Optional.of(tag));
        when(spaceRepository.findByTags(tag)).thenReturn(List.of(space));

        tagService.deleteTag(id);

        assertEquals(0, space.getTags().size());
        verify(spaceRepository).saveAllAndFlush(anyList());
        verify(tagRepository).delete(tag);
        verify(spaceCacheService).forceRefreshCacheForTag(tagName);
    }

    @Test
    void createTagsBulkRaw_SpaceNotFound_ThrowsException() {
        Long spaceId = 1L;
        List<TagCreateDto> emptyList = List.of();

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.createTagsBulkRaw(spaceId, emptyList));
    }

    @Test
    void createTagsBulkRaw_ShouldUseExistingTag_WhenFoundByName() {
        Long spaceId = 1L;
        Space space = new Space();
        space.setId(spaceId);
        space.setTags(new HashSet<>());

        TagCreateDto dto = new TagCreateDto("Existing");
        Tag existingTag = new Tag();
        existingTag.setName("Existing");
        existingTag.setSpaces(new HashSet<>());

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(tagRepository.findByName("Existing")).thenReturn(Optional.of(existingTag));
        when(tagRepository.saveAll(anyList())).thenReturn(List.of(existingTag));

        tagService.createTagsBulkRaw(spaceId, List.of(dto));

        verify(tagRepository).findByName("Existing");
    }

    @Test
    @DisplayName("updateTagName: throw exception if tag not found")
    void updateTagName_NotFound_LambdaCoverage() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> tagService.updateTagName(1L, "new"));
    }

    @Test
    @DisplayName("deleteTag: throw exception if tag not found")
    void deleteTag_NotFound_LambdaCoverage() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> tagService.deleteTag(1L));
    }

    @Test
    @DisplayName("createTagsBulk: success creation and HashSet initialization")
    void createTagsBulkRaw_Success_InitializesHashSet() {
        Long spaceId = 1L;
        String tagName = "NewUniqueTag";
        TagCreateDto dto = new TagCreateDto(tagName);
        Space space = new Space();
        space.setTags(new HashSet<>());

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());
        when(tagRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        tagService.createTagsBulkRaw(spaceId, List.of(dto));

        verify(tagRepository, times(1)).saveAll(any());
        verify(spaceRepository).save(space);
    }

    @Test
    @DisplayName("createTagsBulk: should initialize spaces when tag spaces is null")
    void createTagsBulkRaw_ShouldInitializeSpaces_WhenTagSpacesIsNull() {
        Long spaceId = 1L;
        String tagName = "java";
        TagCreateDto dto = new TagCreateDto(tagName);

        Tag existingTag = new Tag();
        existingTag.setName(tagName);
        existingTag.setSpaces(null);

        Space space = new Space();
        space.setId(spaceId);
        space.setTags(new HashSet<>());

        when(tagRepository.findByName(tagName)).thenReturn(Optional.of(existingTag));
        when(tagRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));

        tagService.createTagsBulkRaw(spaceId, List.of(dto));

        assertNotNull(existingTag.getSpaces());
        assertFalse(existingTag.getSpaces().isEmpty());
        assertEquals(1, existingTag.getSpaces().size());
    }
}
