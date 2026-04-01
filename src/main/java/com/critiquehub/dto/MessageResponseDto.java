package com.critiquehub.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MessageResponseDto(
        Long id,
        String content,
        LocalDateTime timestamp,
        String authorName,
        List<AttachmentResponseDto> attachments
) { }
