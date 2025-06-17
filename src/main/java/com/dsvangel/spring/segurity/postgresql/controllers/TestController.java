package com.dsvangel.spring.segurity.postgresql.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsvangel.spring.segurity.postgresql.models.EReaction;
import com.dsvangel.spring.segurity.postgresql.models.Reaction;
import com.dsvangel.spring.segurity.postgresql.repository.ReactionRepository;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private ReactionRepository reactionRepository;

    @GetMapping("/emojis")
    public ResponseEntity<?> testEmojis() {
        Map<String, Object> response = new HashMap<>();
        
        // Test emojis from enum
        Map<String, String> enumEmojis = new HashMap<>();
        for (EReaction reaction : EReaction.values()) {
            enumEmojis.put(reaction.name(), reaction.getEmoji());
        }
        response.put("enumEmojis", enumEmojis);
        
        // Test emojis from database
        try {
            var dbReactions = reactionRepository.findAll();
            response.put("databaseReactions", dbReactions);
            response.put("databaseCount", dbReactions.size());
        } catch (Exception e) {
            response.put("databaseError", e.getMessage());
        }
        
        // Test direct emoji strings
        response.put("directEmojis", "👍❤️😂😮😢😡");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    public ResponseEntity<?> testPublicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a public endpoint");
        response.put("timestamp", java.time.LocalDateTime.now());
        response.put("authenticated", false);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/reactions-check")
    public ResponseEntity<?> checkReactions() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check if reactions exist
            var reactions = reactionRepository.findAll();
            response.put("reactionCount", reactions.size());
            response.put("reactions", reactions);
            
            if (reactions.isEmpty()) {
                response.put("message", "No reactions found. Run /api/reaction-types/init first");
            } else {
                response.put("message", "Reactions loaded successfully");
            }
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}