package com.dsvangel.spring.segurity.postgresql.controllers;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsvangel.spring.segurity.postgresql.models.Reaction;
import com.dsvangel.spring.segurity.postgresql.models.Tweet;
import com.dsvangel.spring.segurity.postgresql.models.TweetReaction;
import com.dsvangel.spring.segurity.postgresql.models.User;
import com.dsvangel.spring.segurity.postgresql.payload.request.TweetReactionRequest;
import com.dsvangel.spring.segurity.postgresql.payload.response.MessageResponse;
import com.dsvangel.spring.segurity.postgresql.repository.ReactionRepository;
import com.dsvangel.spring.segurity.postgresql.repository.TweetReactionRepository;
import com.dsvangel.spring.segurity.postgresql.repository.TweetRepository;
import com.dsvangel.spring.segurity.postgresql.repository.UserRepository;

@RestController
@RequestMapping("/api/reactions")
public class TweetReactionController {

    @Autowired
    private TweetReactionRepository tweetReactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TweetRepository tweetRepository;
    
    @Autowired
    private ReactionRepository reactionRepository;

    // Este endpoint debe ser público
    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<TweetReaction>> getReactionsByTweet(@PathVariable Long tweetId) {
        try {
            List<TweetReaction> reactions = tweetReactionRepository.findByTweet_Id(tweetId);
            return ResponseEntity.ok(reactions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TweetReaction>> getReactionsByUser(@PathVariable Long userId) {
        try {
            List<TweetReaction> reactions = tweetReactionRepository.findByUser_Id(userId);
            return ResponseEntity.ok(reactions);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrUpdateReaction(@Valid @RequestBody TweetReactionRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = getValidUser(username);
            Tweet tweet = getValidTweet(request.getTweetId());
            Reaction reaction = getValidReaction(request.getReactionId());

            // Check if user already reacted to this tweet
            Optional<TweetReaction> existingReaction = tweetReactionRepository
                .findByUser_IdAndTweet_Id(user.getId(), tweet.getId());

            TweetReaction tweetReaction;
            
            if (existingReaction.isPresent()) {
                // Update existing reaction
                tweetReaction = existingReaction.get();
                tweetReaction.setReaction(reaction);
            } else {
                // Create new reaction
                tweetReaction = new TweetReaction(user, tweet, reaction);
            }

            tweetReactionRepository.save(tweetReaction);
            return ResponseEntity.ok(tweetReaction);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/tweet/{tweetId}")
    public ResponseEntity<?> removeReaction(@PathVariable Long tweetId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = getValidUser(username);
            
            Optional<TweetReaction> existingReaction = tweetReactionRepository
                .findByUser_IdAndTweet_Id(user.getId(), tweetId);

            if (existingReaction.isPresent()) {
                tweetReactionRepository.delete(existingReaction.get());
                return ResponseEntity.ok(new MessageResponse("Reaction removed successfully"));
            } else {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: No reaction found to remove"));
            }

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/tweet/{tweetId}/count")
    public ResponseEntity<Long> getReactionCount(@PathVariable Long tweetId) {
        try {
            long count = tweetReactionRepository.countByTweet_Id(tweetId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(0L);
        }
    }

    @GetMapping("/tweet/{tweetId}/count/{reactionId}")
    public ResponseEntity<Long> getReactionCountByType(@PathVariable Long tweetId, @PathVariable Long reactionId) {
        try {
            long count = tweetReactionRepository.countByTweet_IdAndReaction_Id(tweetId, reactionId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(0L);
        }
    }

    private User getValidUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        return userOpt.get();
    }

    private Tweet getValidTweet(Long tweetId) {
        Optional<Tweet> tweetOpt = tweetRepository.findById(tweetId);
        if (!tweetOpt.isPresent()) {
            throw new RuntimeException("Tweet not found");
        }
        return tweetOpt.get();
    }

    private Reaction getValidReaction(Long reactionId) {
        Optional<Reaction> reactionOpt = reactionRepository.findById(reactionId);
        if (!reactionOpt.isPresent()) {
            throw new RuntimeException("Reaction not found");
        }
        return reactionOpt.get();
    }
}