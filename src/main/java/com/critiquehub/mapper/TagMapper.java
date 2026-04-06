package com.critiquehub.mapper;

import com.critiquehub.dto.TagDto.TagDto;
import com.critiquehub.model.Tag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TagMapper {

    public TagDto toDto(final Tag tag) {
        if (tag == null) {
            return null;
        }

        return new TagDto(
                tag.getId(),
                tag.getName()
        );
    }

    public Tag toEntity(final TagDto dto) {
        if (dto == null) {
            return null;
        }

        Tag tag = new Tag();
        tag.setId(dto.id());
        tag.setName(dto.name());
        return tag;
    }

    public List<TagDto> toDtoList(final List<Tag> tags) {
        if (tags == null) {
            return List.of();
        }
        return tags.stream()
                .map(this::toDto)
                .toList();
    }

    public Set<TagDto> toDtoSet(final Set<Tag> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }
}
