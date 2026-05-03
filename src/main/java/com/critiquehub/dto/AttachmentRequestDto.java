package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request for creating an attachment")
public record AttachmentRequestDto(
        @Schema(description = "URL of the attachment", example = "https://example.com/image.png")
        String url,

        @Schema(description = "ID of the message this attachment belongs to", example = "10")
        Long messageId
) { }
