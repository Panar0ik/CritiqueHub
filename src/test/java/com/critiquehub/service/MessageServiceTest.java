package com.critiquehub.service;

import com.critiquehub.dto.MessageCreateDto;
import com.critiquehub.dto.MessageResponseDto;
import com.critiquehub.mapper.MessageMapper;
import com.critiquehub.model.Message;
import com.critiquehub.model.Space;
import com.critiquehub.model.User;
import com.critiquehub.repository.MessageRepository;
import com.critiquehub.repository.SpaceRepository;
import com.critiquehub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private MessageMapper messageMapper;

    private final Instant fixedInstant = Instant.parse("2026-06-05T02:00:00Z");
    private final ZoneId zoneId = ZoneId.of("UTC");

    @Spy
    private Clock clock = Clock.fixed(fixedInstant, zoneId);

    @InjectMocks
    private MessageService messageService;

    private LocalDateTime fixedDateTime;

    @BeforeEach
    void setUp() {
        fixedDateTime = LocalDateTime.now(clock);
    }

    @Test
    void sendMessage_Success() {
        MessageCreateDto dto = new MessageCreateDto("Hello", 1L, 1L);
        User user = new User();
        Space space = new Space();
        Message message = new Message();
        Message savedMessage = new Message();

        MessageResponseDto expected = new MessageResponseDto(1L, "Hello", fixedDateTime, "user", List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(spaceRepository.findById(1L)).thenReturn(Optional.of(space));
        when(messageMapper.toEntity(dto)).thenReturn(message);
        when(messageRepository.save(message)).thenReturn(savedMessage);
        when(messageMapper.toDto(savedMessage)).thenReturn(expected);

        MessageResponseDto result = messageService.sendMessage(dto);

        assertNotNull(result);
        assertEquals("Hello", result.text());
        assertEquals(fixedDateTime, result.timestamp());
        verify(messageRepository).save(message);
    }

    @Test
    void sendMessage_UserNotFound_ThrowsException() {
        MessageCreateDto dto = new MessageCreateDto("text", 1L, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> messageService.sendMessage(dto));
    }

    @Test
    void sendMessage_SpaceNotFound_ThrowsException() {
        MessageCreateDto dto = new MessageCreateDto("text", 1L, 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(spaceRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> messageService.sendMessage(dto));
    }

    @Test
    void getMessagesBySpace_Success() {
        Long spaceId = 1L;
        Message message = new Message();
        MessageResponseDto dto = new MessageResponseDto(1L, "text", fixedDateTime, "u", List.of());

        when(messageRepository.findBySpaceId(spaceId)).thenReturn(List.of(message));
        when(messageMapper.toDto(message)).thenReturn(dto);

        List<MessageResponseDto> result = messageService.getMessagesBySpace(spaceId);

        assertEquals(1, result.size());
        verify(messageRepository).findBySpaceId(spaceId);
    }

    @Test
    void updateMessage_Success() {
        Long id = 1L;
        String newContent = "updated";
        Message message = new Message();
        MessageResponseDto expected = new MessageResponseDto(1L, newContent, fixedDateTime, "u", List.of());

        when(messageRepository.findById(id)).thenReturn(Optional.of(message));

        when(messageMapper.toDto(message)).thenReturn(expected);

        MessageResponseDto result = messageService.updateMessage(id, newContent);

        assertEquals(newContent, result.text());
    }

    @Test
    void updateMessage_NotFound_ThrowsException() {
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> messageService.updateMessage(1L, "content"));
    }

    @Test
    void deleteMessage_Success() {
        Long id = 1L;
        when(messageRepository.existsById(id)).thenReturn(true);

        messageService.deleteMessage(id);

        verify(messageRepository).deleteById(id);
    }

    @Test
    void deleteMessage_NotFound_ThrowsException() {
        when(messageRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> messageService.deleteMessage(1L));
    }
}
