package com.critiquehub.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.critiquehub.model.Space;
import com.critiquehub.model.ChatMessage;
import com.critiquehub.repository.CommunityRepository;
import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityRepository repository;

    /** Retrieves all available interest groups. */
    @GetMapping("/spaces")
    public List<Space> getAllSpaces() {
        return repository.findAllSpaces();
    }

    /** Retrieves chat history for a specific space. */
    @GetMapping("/spaces/{id}/messages")
    public List<ChatMessage> getMessages(@PathVariable Long id) {
        return repository.findMessagesBySpace(id);
    }

    /** Sends a new message to a space. */
    @PostMapping("/messages")
    public void sendMessage(@RequestBody ChatMessage message) {
        repository.saveMessage(message);
    }
}
