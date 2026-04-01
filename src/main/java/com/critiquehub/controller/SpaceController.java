package com.critiquehub.controller;

import com.critiquehub.dto.SpaceCreateDto;
import com.critiquehub.dto.SpaceResponseDto;
import com.critiquehub.service.SpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @PostMapping
    public ResponseEntity<SpaceResponseDto> create(final @RequestBody SpaceCreateDto dto) {
        SpaceResponseDto createdSpace = spaceService.createSpace(dto);
        return new ResponseEntity<>(createdSpace, HttpStatus.CREATED);
    }

    @GetMapping
    public List<SpaceResponseDto> getAll() {
        return spaceService.getAllSpaces();
    }

    @GetMapping("/{id}")
    public SpaceResponseDto getById(final @PathVariable Long id) {
        return spaceService.getById(id);
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
