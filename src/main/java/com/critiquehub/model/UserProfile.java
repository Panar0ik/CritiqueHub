package com.critiquehub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Id;

@Entity
public class UserProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bio;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserProfile() { }

    // Добавь эти методы:
    public Long getId() {
        return id;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(final String bioParam) {
        this.bio = bioParam;
    }
    public void setUser(final User userParam) {
        this.user = userParam;
    }
}
