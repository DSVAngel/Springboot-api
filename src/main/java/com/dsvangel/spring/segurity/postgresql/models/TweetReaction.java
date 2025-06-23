package com.dsvangel.spring.segurity.postgresql.models;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "tweet_reactions",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tweet_id"})
    }
)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TweetReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Tweet tweet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reaction_id", nullable = false)
    @JsonManagedReference
    private Reaction reaction;

    public TweetReaction() {}

    public TweetReaction(User user, Tweet tweet, Reaction reaction) {
        this.user = user;
        this.tweet = tweet;
        this.reaction = reaction;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public Reaction getReaction() {
        return reaction;
    }

    public void setReaction(Reaction reaction) {
        this.reaction = reaction;
    }

    // Helper methods for backward compatibility
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    public Long getTweetId() {
        return tweet != null ? tweet.getId() : null;
    }

    public Long getReactionId() {
        return reaction != null ? reaction.getId() : null;
    }
}