package com.critiquehub.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "spaces")
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String category;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> messages = new ArrayList<>();

    public Space() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public void setId(final Long idParam) {
        this.id = idParam;
    }

    public void setName(final String nameParam) {
        this.name = nameParam;
    }

    public void setDescription(final String descriptionParam) {
        this.description = descriptionParam;
    }

    public void setCategory(final String categoryParam) {
        this.category = categoryParam;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void addMessage(final ChatMessage messageParam) {
        if (messageParam != null) {
            this.messages.add(messageParam);
            messageParam.setSpace(this);
        }
    }
}
