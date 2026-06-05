package com.critiquehub.controller;

import com.critiquehub.dto.MessageCreateDto;
import com.critiquehub.dto.MessageResponseDto;
import com.critiquehub.service.MessageService;
import com.critiquehub.util.websocket.SpaceWebSocketHandler;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MessageController {

    private final MessageService messageService;
    private final SpaceWebSocketHandler webSocketHandler;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Send a message")
    public MessageResponseDto sendMessage(final @RequestBody MessageCreateDto dto) {
        final MessageResponseDto savedMessage = messageService.sendMessage(dto);

        final Map<String, Object> wsPayload = Map.of(
                "type", "NEW_MESSAGE",
                "message", savedMessage
        );
        webSocketHandler.broadcastToSpace(String.valueOf(dto.spaceId()), wsPayload);

        return savedMessage;
    }

    @GetMapping("/space/{spaceId}")
    @Operation(summary = "Get messages by space ID")
    public List<MessageResponseDto> getBySpace(final @PathVariable Long spaceId) {
        return messageService.getMessagesBySpace(spaceId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit message content")
    public MessageResponseDto editMessage(final @PathVariable Long id,
                                          final @RequestParam Long spaceId,
                                          final @RequestBody String newContent) {
        final MessageResponseDto updatedMessage = messageService.updateMessage(id, newContent);

        final Map<String, Object> wsPayload = Map.of("type", "UPDATE_MESSAGES");
        webSocketHandler.broadcastToSpace(String.valueOf(spaceId), wsPayload);

        return updatedMessage;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete message")
    public void delete(final @PathVariable Long id, final @RequestParam Long spaceId) {
        messageService.deleteMessage(id);

        final Map<String, Object> wsPayload = Map.of("type", "UPDATE_MESSAGES");
        webSocketHandler.broadcastToSpace(String.valueOf(spaceId), wsPayload);
    }
}
