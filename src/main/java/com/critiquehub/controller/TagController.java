package com.critiquehub.controller;

import com.critiquehub.dto.TagDto;
import com.critiquehub.mapper.TagMapper;
import com.critiquehub.model.Tag;
import com.critiquehub.service.TagService;
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
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagDto createTag(final @RequestBody TagDto tagDto) {
        Tag savedTag = tagService.saveTag(tagDto);

        return tagMapper.toDto(savedTag);
    }

    @GetMapping
    public List<Tag> getAll() {
        return tagService.getAllTags();
    }

    @GetMapping("/search")
    public Tag getByName(final @RequestParam String name) {
        return tagService.getByName(name);
    }

    @PutMapping("/{id}")
    public Tag update(final @PathVariable Long id, final @RequestBody String newName) {
        return tagService.updateTagName(id, newName);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(final @PathVariable Long id) {
        tagService.deleteTag(id);
    }
}
