package com.critiquehub.dto;

public record AttachmentRequestDto(
        String url,
        Long messageId
) { }
