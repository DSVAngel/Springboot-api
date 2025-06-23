package com.dsvangel.spring.segurity.postgresql.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonInclude;

@Entity
@Table(name = "reactions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Reaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private EReaction description;

    @Column(length = 50, columnDefinition = "TEXT")
    private String emoji;

    public Reaction() {}

    public Reaction(EReaction description) {
        this.description = description;
        this.emoji = description.getEmoji();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EReaction getDescription() {
        return description;
    }

    public void setDescription(EReaction description) {
        this.description = description;
        if (description != null) {
            this.emoji = description.getEmoji();
        }
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    // Helper method to get display text
    public String getDisplayText() {
        return (emoji != null ? emoji : "") + " " + (description != null ? description.name().toLowerCase() : "");
    }
}