package com.critiquehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "spaces")
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "space_tags",
            joinColumns = @JoinColumn(name = "space_id"),
            inverseJoinColumns = @JoinColumn(name = "tags_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
