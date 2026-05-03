package com.critiquehub.service;

import com.critiquehub.dto.AttachmentResponseDto;
import com.critiquehub.mapper.AttachmentMapper;
import com.critiquehub.model.Attachment;
import com.critiquehub.model.Message;
import com.critiquehub.repository.AttachmentRepository;
import com.critiquehub.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private AttachmentService attachmentService;

    @Test
    void saveAttachment_Success() {
        Long messageId = 1L;
        String filePath = "test/path.jpg";
        Message message = new Message();
        Attachment attachment = new Attachment();
        AttachmentResponseDto expectedDto = new AttachmentResponseDto(1L, filePath);

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
        when(attachmentMapper.toDto(attachment)).thenReturn(expectedDto);

        AttachmentResponseDto result = attachmentService.saveAttachment(filePath, messageId);

        assertNotNull(result);
        assertEquals(filePath, result.url());
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void saveAttachment_MessageNotFound_ThrowsException() {
        when(messageRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                attachmentService.saveAttachment("path", 1L));
    }

    @Test
    void getByMessageId_Success() {
        Long messageId = 1L;
        Attachment attachment = new Attachment();
        AttachmentResponseDto dto = new AttachmentResponseDto(1L, "url");

        when(attachmentRepository.findByMessageId(messageId)).thenReturn(List.of(attachment));
        when(attachmentMapper.toDto(attachment)).thenReturn(dto);

        List<AttachmentResponseDto> result = attachmentService.getByMessageId(messageId);

        assertEquals(1, result.size());
        assertEquals(dto, result.get(0));
    }

    @Test
    void updateAttachmentPath_Success() {
        Long id = 1L;
        String newPath = "new/path.png";
        Attachment attachment = new Attachment();
        AttachmentResponseDto expectedDto = new AttachmentResponseDto(id, newPath);

        when(attachmentRepository.findById(id)).thenReturn(Optional.of(attachment));
        when(attachmentRepository.save(attachment)).thenReturn(attachment);
        when(attachmentMapper.toDto(attachment)).thenReturn(expectedDto);

        AttachmentResponseDto result = attachmentService.updateAttachmentPath(id, newPath);

        assertEquals(newPath, result.url());
        verify(attachmentRepository).save(attachment);
    }

    @Test
    void updateAttachmentPath_NotFound_ThrowsException() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                attachmentService.updateAttachmentPath(1L, "path"));

        assertEquals("Attachment not found", ex.getMessage());
    }

    @Test
    void deleteAttachment_Success() {
        Long id = 1L;

        attachmentService.deleteAttachment(id);

        verify(attachmentRepository, times(1)).deleteById(id);
    }
}
