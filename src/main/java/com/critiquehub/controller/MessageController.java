package com.critiquehub.controller;

import com.critiquehub.dto.MessageDto.MessageCreateDto;
import com.critiquehub.dto.MessageDto.MessageResponseDto;
import com.critiquehub.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseDto sendMessage(final @RequestBody MessageCreateDto dto) {
        return messageService.sendMessage(dto);
    }

    @GetMapping("/space/{spaceId}")
    public List<MessageResponseDto> getBySpace(final @PathVariable Long spaceId) {
        return messageService.getMessagesBySpace(spaceId);
    }

    @PutMapping("/{id}")
    public MessageResponseDto editMessage(final @PathVariable Long id, final @RequestBody String newContent) {
        return messageService.updateMessage(id, newContent);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(final @PathVariable Long id) {
        messageService.deleteMessage(id);
    }
}
