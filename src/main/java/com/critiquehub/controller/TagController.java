package com.critiquehub.controller;

import com.critiquehub.dto.TagCreateDto;
import com.critiquehub.dto.TagDto;
import com.critiquehub.mapper.TagMapper;
import com.critiquehub.model.Tag;
import com.critiquehub.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tags", description = "Tag management APIs")
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new tag")
    public TagDto createTag(final @RequestBody TagDto tagDto) {
        final Tag savedTag = tagService.saveTag(tagDto);
        return tagMapper.toDto(savedTag);
    }

    @GetMapping
    @Operation(summary = "Get all tags")
    public List<TagDto> getAll() {
        return tagService.getAllTags().stream()
                .map(tagMapper::toDto)
                .toList();
    }

    @GetMapping("/search")
    @Operation(summary = "Find tag by name")
    public TagDto getByName(final @RequestParam String name) {
        final Tag tag = tagService.getByName(name);
        return tagMapper.toDto(tag);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tag name")
    public Tag update(final @PathVariable Long id, final @RequestBody String newName) {
        return tagService.updateTagName(id, newName);
    }

    @PostMapping("/bulk/{spaceId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Bulk create tags for space")
    public List<TagDto> createTagsBulk(
            final @PathVariable Long spaceId,
            final @RequestBody List<TagCreateDto> tagDtos
    ) {
        return tagService.createTagsBulk(spaceId, tagDtos);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete tag")
    public void delete(final @PathVariable Long id) {
        tagService.deleteTag(id);
    }
}
