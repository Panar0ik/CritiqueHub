package com.critiquehub.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;

    public User() { }

    // Добавь эти методы:
    public Long getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(final String usernameParam) {
        this.username = usernameParam;
    }

    public UserProfile getProfile() {
        return profile;
    }
    public void setProfile(final UserProfile profileParam) {
        this.profile = profileParam;
    }
}
