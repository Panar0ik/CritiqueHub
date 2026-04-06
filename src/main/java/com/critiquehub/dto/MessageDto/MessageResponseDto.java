package com.critiquehub.dto.MessageDto;

import com.critiquehub.dto.AttachmentDto.AttachmentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record MessageResponseDto(
        Long id,
        String text,
        LocalDateTime timestamp,
        String authorName,
        List<AttachmentResponseDto> attachments
) { }
