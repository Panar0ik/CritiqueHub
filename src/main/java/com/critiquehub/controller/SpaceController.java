package com.critiquehub.controller;

import com.critiquehub.dto.SpaceDto;
import com.critiquehub.model.ChatMessage;
import com.critiquehub.service.SpaceService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class SpaceController {

    private final SpaceService spaceService;

    public SpaceController(final SpaceService spaceServiceParam) {
        this.spaceService = spaceServiceParam;
    }

    @GetMapping("/spaces")
    public List<SpaceDto> getAllSpaces() {
        return spaceService.getAllSpaces();
    }

    @GetMapping("/spaces/{id}")
    public SpaceDto getSpace(@PathVariable final Long id) {
        return spaceService.getSpaceById(id);
    }

    @GetMapping("/spaces/{id}/messages")
    public List<ChatMessage> getMessages(@PathVariable final Long id) {
        return spaceService.getMessagesBySpace(id);
    }

    @PostMapping("/spaces/{id}/messages")
    public void sendMessage(
            @PathVariable final Long id,
            @RequestBody final ChatMessage messageParam) {
        spaceService.postMessage(id, messageParam);
    }

    @PostMapping("/spaces")
    public void addSpace(@RequestBody final SpaceDto spaceDtoParam) {
        spaceService.createSpace(spaceDtoParam);
    }

    @DeleteMapping("/spaces/{id}")
    public void deleteSpace(@PathVariable final Long id) {
        spaceService.deleteSpace(id);
    }

    @PostMapping("/test-transactional")
    public void testTransaction() {
        // Пункт 6: Метод для демонстрации отката транзакции
        spaceService.demonstrateTransaction();
    }
}
