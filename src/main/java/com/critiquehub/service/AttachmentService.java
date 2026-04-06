package com.critiquehub.service;

import com.critiquehub.dto.AttachmentDto.AttachmentResponseDto;
import com.critiquehub.mapper.AttachmentMapper;
import com.critiquehub.model.Attachment;
import com.critiquehub.repository.AttachmentRepository;
import com.critiquehub.repository.MessageRepository;
import com.critiquehub.model.Message;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final MessageRepository messageRepository;

    @Transactional
    public AttachmentResponseDto saveAttachment(final String filePath, final Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message not found"));

        Attachment attachment = new Attachment();
        attachment.setUrl(filePath);
        attachment.setMessage(message);

        Attachment saved = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(saved);
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
