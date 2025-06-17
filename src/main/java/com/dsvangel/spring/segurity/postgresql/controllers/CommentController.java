package com.dsvangel.spring.segurity.postgresql.controllers;

import java.util.List;
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

import com.dsvangel.spring.segurity.postgresql.models.Comment;
import com.dsvangel.spring.segurity.postgresql.models.Tweet;
import com.dsvangel.spring.segurity.postgresql.models.User;
import com.dsvangel.spring.segurity.postgresql.payload.request.CommentRequest;
import com.dsvangel.spring.segurity.postgresql.payload.response.MessageResponse;
import com.dsvangel.spring.segurity.postgresql.repository.CommentRepository;
import com.dsvangel.spring.segurity.postgresql.repository.TweetRepository;
import com.dsvangel.spring.segurity.postgresql.repository.UserRepository;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TweetRepository tweetRepository;

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<Page<Comment>> getCommentsByTweet(@PathVariable Long tweetId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByTweet_IdAndParentCommentIsNull(tweetId, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<Comment>> getRepliesByComment(@PathVariable Long commentId) {
        List<Comment> replies = commentRepository.findByParentComment_Id(commentId);
        return ResponseEntity.ok(replies);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Comment>> getCommentsByUser(@PathVariable Long userId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByUser_Id(userId, pageable);
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            User user = getValidUser(username);
            Tweet tweet = getValidTweet(request.getTweetId());
            
            Comment comment = new Comment(request.getContent(), tweet, user);
            
            // If it's a reply to another comment
            if (request.getParentCommentId() != null) {
                Comment parentComment = getValidComment(request.getParentCommentId());
                comment.setParentComment(parentComment);
            }

            commentRepository.save(comment);
            return ResponseEntity.ok(comment);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @Valid @RequestBody CommentRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = getValidUser(username);

            Optional<Comment> commentOpt = commentRepository.findById(commentId);
            if (!commentOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Comment not found"));
            }

            Comment comment = commentOpt.get();
            
            // Check if user owns this comment
            if (!comment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: You can only edit your own comments"));
            }

            comment.setContent(request.getContent());
            commentRepository.save(comment);
            
            return ResponseEntity.ok(comment);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = getValidUser(username);

            Optional<Comment> commentOpt = commentRepository.findById(commentId);
            if (!commentOpt.isPresent()) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Comment not found"));
            }

            Comment comment = commentOpt.get();
            
            // Check if user owns this comment
            if (!comment.getUser().getId().equals(user.getId())) {
                return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: You can only delete your own comments"));
            }

            commentRepository.delete(comment);
            return ResponseEntity.ok(new MessageResponse("Comment deleted successfully"));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/tweet/{tweetId}/count")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long tweetId) {
        long count = commentRepository.countByTweet_Id(tweetId);
        return ResponseEntity.ok(count);
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

    private Comment getValidComment(Long commentId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            throw new RuntimeException("Comment not found");
        }
        return commentOpt.get();
    }
}