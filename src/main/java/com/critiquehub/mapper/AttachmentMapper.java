package com.critiquehub.mapper;

import com.critiquehub.dto.AttachmentDto.AttachmentResponseDto;
import com.critiquehub.model.Attachment;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {
    public AttachmentResponseDto toDto(final Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        return new AttachmentResponseDto(
                attachment.getId(),
                attachment.getUrl()
        );
    }

    public Attachment toEntity(final String url) {
        if (url == null) {
            return null;
        }

        Attachment attachment = new Attachment();
        attachment.setUrl(url);
        return attachment;
    }
}
