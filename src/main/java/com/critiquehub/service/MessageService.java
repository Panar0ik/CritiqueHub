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
import com.critiquehub.util.aspect.LogExecutionTime;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;
    private final MessageMapper messageMapper;

    @LogExecutionTime
    @Transactional
    public MessageResponseDto sendMessage(final MessageCreateDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + dto.userId()));

        Space space = spaceRepository.findById(dto.spaceId())
                .orElseThrow(() -> new EntityNotFoundException("Space not found with id " + dto.spaceId()));

        Message message = messageMapper.toEntity(dto);

        message.setUser(user);
        message.setSpace(space);
        message.setTimestamp(LocalDateTime.now(ZoneOffset.UTC));

        Message savedMessage = messageRepository.save(message);
        return messageMapper.toDto(savedMessage);
    }

    @LogExecutionTime
    @Transactional
    public List<MessageResponseDto> getMessagesBySpace(final Long spaceId) {
        return messageRepository.findBySpaceId(spaceId).stream()
                .map(messageMapper::toDto)
                .toList();
    }

    @LogExecutionTime
    @Transactional
    public MessageResponseDto updateMessage(final Long id, final String newContent) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Message not found with id " + id));

        message.setText(newContent);

        return messageMapper.toDto(messageRepository.save(message));
    }

    @LogExecutionTime
    @Transactional
    public void deleteMessage(final Long id) {
        if (!messageRepository.existsById(id)) {
            throw new EntityNotFoundException("Message not found with id " + id);
        }
        messageRepository.deleteById(id);
    }
}
