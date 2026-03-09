package com.critiquehub.service;

import com.critiquehub.dto.SpaceDto;
import com.critiquehub.mapper.SpaceMapper;
import com.critiquehub.model.ChatMessage;
import com.critiquehub.model.Space;
import com.critiquehub.repository.SpaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMapper spaceMapper;

    public SpaceService(
            final SpaceRepository spaceRepoParam,
            final SpaceMapper mapperParam
    ) {
        this.spaceRepository = spaceRepoParam;
        this.spaceMapper = mapperParam;
    }

    public List<SpaceDto> getAllSpaces() {
        return spaceRepository.findAll().stream()
                .map(spaceMapper::toDto)
                .toList();
    }

    public SpaceDto getSpaceById(final Long idParam) {
        return spaceRepository.findById(idParam)
                .map(spaceMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public void postMessage(final Long idParam, final ChatMessage messageParam) {
        Space space = spaceRepository.findById(idParam)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        space.addMessage(messageParam);
    }

    public List<ChatMessage> getMessagesBySpace(final Long idParam) {
        return spaceRepository.findById(idParam)
                .map(Space::getMessages)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public void createSpace(final SpaceDto dtoParam) {
        Space space = new Space();
        space.setName(dtoParam.name());
        space.setCategory(dtoParam.category());
        spaceRepository.save(space);
    }

    public void deleteSpace(final Long idParam) {
        spaceRepository.deleteById(idParam);
    }

    @Transactional
    public void demonstrateTransaction() {
        Space s1 = new Space();
        s1.setName("Transaction Test");
        spaceRepository.save(s1);
    }
}
