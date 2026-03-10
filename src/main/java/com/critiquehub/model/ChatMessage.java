package com.critiquehub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id")
    @JsonIgnore
    private Space space;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    public Long getId() {
        return id;
    }

    public void setId(final Long idParam) {
        this.id = idParam;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String contentParam) {
        this.content = contentParam;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final LocalDateTime timestampParam) {
        this.timestamp = timestampParam;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(final Space spaceParam) {
        this.space = spaceParam;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(final User senderParam) {
        this.sender = senderParam;
    }
}
