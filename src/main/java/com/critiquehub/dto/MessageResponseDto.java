package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response containing message details and attachments")
public record MessageResponseDto(
        @Schema(description = "Message ID", example = "100")
        Long id,

        @Schema(description = "Message text content", example = "Check this attachment out!")
        String text,

        @Schema(description = "Time when message was sent")
        LocalDateTime timestamp,

        @Schema(description = "Username of the author", example = "john_doe")
        String authorName,

        @Schema(description = "List of message attachments")
        List<AttachmentResponseDto> attachments
) { }
