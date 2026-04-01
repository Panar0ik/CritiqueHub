package com.critiquehub.mapper;

import com.critiquehub.dto.AttachmentResponseDto;
import com.critiquehub.dto.MessageCreateDto;
import com.critiquehub.dto.MessageResponseDto;
import com.critiquehub.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageMapper {
    private final AttachmentMapper attachmentMapper;

    public MessageResponseDto toDto(final Message message) {
        if (message == null) {
            return null;
        }

        String authorName = (message.getUser() != null)
                ? message.getUser().getUsername()
                : "System/Deleted";

        List<AttachmentResponseDto> attachmentDto = Collections.emptyList();
        if (message.getAttachments() != null) {
            attachmentDto = message.getAttachments().stream()
                    .map(attachmentMapper::toDto)
                    .toList();
        }

        return new MessageResponseDto(
                message.getId(),
                message.getText(),
                message.getTimestamp(),
                authorName,
                attachmentDto
        );
    }

    public Message toEntity(final MessageCreateDto dto) {
        if (dto == null) {
            return null;
        }

        Message message = new Message();
        message.setText(dto.Text());
        return message;
    }
}
