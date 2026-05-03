package com.critiquehub.controller;

import com.critiquehub.dto.AttachmentRequestDto;
import com.critiquehub.dto.AttachmentResponseDto;
import com.critiquehub.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@Tag(name = "Attachments", description = "Attachment management APIs")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(final AttachmentService service) {
        this.attachmentService = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new attachment")
    public AttachmentResponseDto create(final @RequestBody AttachmentRequestDto requestDto) {
        return attachmentService.saveAttachment(requestDto.url(), requestDto.messageId());
    }

    @GetMapping("/message/{messageId}")
    @Operation(summary = "Get attachments by message ID")
    public List<AttachmentResponseDto> getByMessage(final @PathVariable Long messageId) {
        return attachmentService.getByMessageId(messageId);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update attachment file path")
    public AttachmentResponseDto update(final @PathVariable Long id, final @RequestBody String newFilePath) {
        return attachmentService.updateAttachmentPath(id, newFilePath);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete attachment")
    public void delete(final @PathVariable Long id) {
        attachmentService.deleteAttachment(id);
    }
}
