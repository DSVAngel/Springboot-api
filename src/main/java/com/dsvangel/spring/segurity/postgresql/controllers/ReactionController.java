package com.dsvangel.spring.segurity.postgresql.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsvangel.spring.segurity.postgresql.models.EReaction;
import com.dsvangel.spring.segurity.postgresql.models.Reaction;
import com.dsvangel.spring.segurity.postgresql.payload.response.MessageResponse;
import com.dsvangel.spring.segurity.postgresql.repository.ReactionRepository;

@RestController
@RequestMapping("/api/reaction-types")
public class ReactionController {

    @Autowired
    private ReactionRepository reactionRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Reaction>> getAllReactions() {
        List<Reaction> reactions = reactionRepository.findAll();
        return ResponseEntity.ok(reactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReactionById(@PathVariable Long id) {
        Optional<Reaction> reactionOpt = reactionRepository.findById(id);
        if (reactionOpt.isPresent()) {
            return ResponseEntity.ok(reactionOpt.get());
        } else {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Reaction not found"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReaction(@RequestBody ReactionRequest reactionRequest) {
        try {
            EReaction eReaction = EReaction.valueOf(reactionRequest.getDescription().toUpperCase());
            
            // Check if reaction type already exists
            Optional<Reaction> existingReaction = reactionRepository.findByDescription(eReaction);
            if (existingReaction.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Reaction type already exists"));
            }

            Reaction reaction = new Reaction(eReaction);
            reactionRepository.save(reaction);
            
            return ResponseEntity.ok(reaction);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Invalid reaction type. Available: " + 
                    java.util.Arrays.toString(EReaction.values())));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReaction(@PathVariable Long id) {
        try {
            Optional<Reaction> reactionOpt = reactionRepository.findById(id);
            if (!reactionOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Reaction not found"));
            }

            reactionRepository.delete(reactionOpt.get());
            return ResponseEntity.ok(new MessageResponse("Reaction deleted successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/init")
    public ResponseEntity<?> initializeDefaultReactions() {
        try {
            // Create or update default reactions
            for (EReaction eReaction : EReaction.values()) {
                Optional<Reaction> existingReaction = reactionRepository.findByDescription(eReaction);
                Reaction reaction;
                if (existingReaction.isPresent()) {
                    // Update existing reaction with new emoji
                    reaction = existingReaction.get();
                    reaction.setDescription(eReaction); // This will update the emoji
                } else {
                    // Create new reaction
                    reaction = new Reaction(eReaction);
                }
                reactionRepository.save(reaction);
            }
            
            return ResponseEntity.ok(new MessageResponse("Default emoji reactions initialized/updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableReactions() {
        try {
            java.util.Map<String, String> availableReactions = new java.util.HashMap<>();
            for (EReaction eReaction : EReaction.values()) {
                availableReactions.put(eReaction.name(), eReaction.getEmoji());
            }
            return ResponseEntity.ok(availableReactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    // Inner class for request body
    public static class ReactionRequest {
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}