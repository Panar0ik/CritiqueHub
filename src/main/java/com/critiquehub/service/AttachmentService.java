package com.critiquehub.service;

import com.critiquehub.dto.AttachmentResponseDto;
import com.critiquehub.mapper.AttachmentMapper;
import com.critiquehub.model.Attachment;
import com.critiquehub.repository.AttachmentRepository;
import com.critiquehub.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;

    @Transactional
    public AttachmentResponseDto saveAttachment(final String filePath, final Message message) {
        Attachment attachment = new Attachment();
        attachment.setUrl(filePath);
        attachment.setMessage(message);

        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Transactional(readOnly = true)
    public List<AttachmentResponseDto> getByMessageId(final Long messageId) {
        return attachmentRepository.findByMessageId(messageId).stream()
                .map(attachmentMapper::toDto)
                .toList();
    }

    @Transactional
    public AttachmentResponseDto updateAttachmentPath(final Long id, final String newPath) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));
        attachment.setUrl(newPath);
        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Transactional
    public void deleteAttachment(final Long id) {
        attachmentRepository.deleteById(id);
    }
}
