package com.critiquehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing attachment details")
public record AttachmentResponseDto(
        @Schema(description = "Attachment unique ID", example = "1")
        Long id,

        @Schema(description = "URL of the attachment", example = "https://example.com/image.png")
        String url
) { }
