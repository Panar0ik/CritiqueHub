package com.critiquehub.controller;

import com.critiquehub.dto.SpaceCreateDto;
import com.critiquehub.dto.SpaceResponseDto;
import com.critiquehub.service.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final SpaceService spaceService;

    @PostMapping
    public ResponseEntity<SpaceResponseDto> create(final @Valid @RequestBody SpaceCreateDto dto) {
        SpaceResponseDto created = spaceService.createSpace(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<SpaceResponseDto> getAll() {
        return spaceService.getAllSpaces();
    }

    @GetMapping("/{id}")
    public SpaceResponseDto getById(final @PathVariable Long id) {
        return spaceService.getById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<SpaceResponseDto>> search(
            final @RequestParam String tag,
            final @PageableDefault(size = DEFAULT_PAGE_SIZE) Pageable pageable
    ) {
        return ResponseEntity.ok(spaceService.getSpacesByTag(tag, pageable));
    }

    @PutMapping("/{id}")
    public SpaceResponseDto update(final @PathVariable Long id, final @RequestBody SpaceCreateDto dto) {
        return spaceService.updateSpace(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(final @PathVariable Long id) {
        spaceService.deleteSpace(id);
    }
}
