package com.critiquehub.controller;

import com.critiquehub.dto.AttachmentResponseDto;
import com.critiquehub.dto.MessageShortDto;
import com.critiquehub.service.AttachmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(final AttachmentService service) {
        this.attachmentService = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponseDto create(
            final @RequestParam String filePath,
            final @RequestBody MessageShortDto messageDto) {

        return attachmentService.saveAttachment(filePath, messageDto.id());
    }

    @GetMapping("/message/{messageId}")
    public List<AttachmentResponseDto> getByMessage(final @PathVariable Long messageId) {
        return attachmentService.getByMessageId(messageId);
    }

    @PutMapping("/{id}")
    public AttachmentResponseDto update(final @PathVariable Long id, final @RequestBody String newFilePath) {
        return attachmentService.updateAttachmentPath(id, newFilePath);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(final @PathVariable Long id) {
        attachmentService.deleteAttachment(id);
    }
}
