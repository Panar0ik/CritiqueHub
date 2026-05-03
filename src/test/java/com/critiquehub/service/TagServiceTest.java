package com.critiquehub.service;

import com.critiquehub.dto.TagCreateDto;
import com.critiquehub.dto.TagDto;
import com.critiquehub.model.Space;
import com.critiquehub.model.Tag;
import com.critiquehub.model.User;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.TagRepository;
import com.critiquehub.util.cache.SpaceCacheService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private SpaceRepository spaceRepository;

    @Mock
    private SpaceCacheService spaceCacheService;

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
    void createTagsBulk_Success() {
        Long spaceId = 1L;
        Space space = new Space();
        space.setId(spaceId);
        space.setName("Test Space");
        User owner = new User();
        owner.setUsername("admin");
        space.setOwner(owner);
        space.setTags(new HashSet<>());

        List<TagCreateDto> dtos = List.of(new TagCreateDto("tag1"), new TagCreateDto("tag2"));

        Tag tag1 = new Tag();
        tag1.setId(10L);
        tag1.setName("tag1");
        tag1.setSpaces(new HashSet<>(Set.of(space)));

        Tag tag2 = new Tag();
        tag2.setId(11L);
        tag2.setName("tag2");
        tag2.setSpaces(new HashSet<>(Set.of(space)));

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(tagRepository.saveAll(anyList())).thenReturn(List.of(tag1, tag2));

        List<TagDto> result = tagService.createTagsBulk(spaceId, dtos);

        assertEquals(2, result.size());
        assertEquals("tag1", result.get(0).name());
        verify(tagRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createTagsBulk_SpaceNotFound_ThrowsException() {
        Long spaceId = 1L;
        List<TagCreateDto> emptyList = List.of();

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tagService.createTagsBulk(spaceId, emptyList));
    }
}
