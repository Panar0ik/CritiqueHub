package com.critiquehub.service;

import com.critiquehub.repository.TagRepository;
import com.critiquehub.model.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Tag getByName(final String name) {
        return tagRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Tag not found: " + name));
    }

    @Transactional
    public Tag saveTag(final String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    return tagRepository.save(tag);
                });
    }

    @Transactional
    public Tag updateTagName(final Long id, final String newName) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));

        // Проверяем, не занято ли имя другим тегом
        tagRepository.findByName(newName).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new RuntimeException("Tag with name '" + newName + "' already exists");
            }
        });

        tag.setName(newName);
        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(final Long id) {
        tagRepository.deleteById(id);
    }
}
