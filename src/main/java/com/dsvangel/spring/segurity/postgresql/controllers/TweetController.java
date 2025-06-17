package com.dsvangel.spring.segurity.postgresql.controllers;

import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsvangel.spring.segurity.postgresql.models.Tweet;
import com.dsvangel.spring.segurity.postgresql.models.User;
import com.dsvangel.spring.segurity.postgresql.payload.response.MessageResponse;
import com.dsvangel.spring.segurity.postgresql.repository.TweetRepository;
import com.dsvangel.spring.segurity.postgresql.repository.UserRepository;

@RestController
@RequestMapping("/api/tweets")
public class TweetController {

    @Autowired
    private TweetRepository tweetRepository;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/all")
    public Page<Tweet> getAllTweets(Pageable pageable) {
        return tweetRepository.findAllByOrderByIdDesc(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTweetById(@PathVariable Long id) {
        Optional<Tweet> tweetOpt = tweetRepository.findById(id);
        if (tweetOpt.isPresent()) {
            return ResponseEntity.ok(tweetOpt.get());
        } else {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Tweet not found"));
        }
    }

    @GetMapping("/user/{userId}")
    public Page<Tweet> getTweetsByUser(@PathVariable Long userId, Pageable pageable) {
        return tweetRepository.findByPostedBy_IdOrderByIdDesc(userId, pageable);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTweet(@Valid @RequestBody Tweet tweet) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = getValidUser(username);
            
            Tweet newTweet = new Tweet(tweet.getTweet());
            newTweet.setPostedBy(user);
            
            tweetRepository.save(newTweet);
            return ResponseEntity.ok(newTweet);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTweet(@PathVariable Long id, @Valid @RequestBody Tweet tweetUpdate) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = getValidUser(username);

            Optional<Tweet> tweetOpt = tweetRepository.findById(id);
            if (!tweetOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Tweet not found"));
            }

            Tweet tweet = tweetOpt.get();
            
            // Check if user owns this tweet
            if (!tweet.getPostedBy().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: You can only edit your own tweets"));
            }

            tweet.setTweet(tweetUpdate.getTweet());
            tweetRepository.save(tweet);
            
            return ResponseEntity.ok(tweet);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTweet(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = getValidUser(username);

            Optional<Tweet> tweetOpt = tweetRepository.findById(id);
            if (!tweetOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Tweet not found"));
            }

            Tweet tweet = tweetOpt.get();
            
            // Check if user owns this tweet
            if (!tweet.getPostedBy().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: You can only delete your own tweets"));
            }

            tweetRepository.delete(tweet);
            return ResponseEntity.ok(new MessageResponse("Tweet deleted successfully"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    private User getValidUser(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        return userOpt.get();
    }
}