package com.dsvangel.spring.segurity.postgresql.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsvangel.spring.segurity.postgresql.models.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Get comments for a specific tweet (only top-level comments, not replies)
    Page<Comment> findByTweet_IdAndParentCommentIsNull(Long tweetId, Pageable pageable);
    
    // Get all comments for a tweet (including replies)
    Page<Comment> findByTweet_Id(Long tweetId, Pageable pageable);
    
    // Get replies to a specific comment
    List<Comment> findByParentComment_Id(Long parentCommentId);
    
    // Get comments by user
    Page<Comment> findByUser_Id(Long userId, Pageable pageable);
    
    // Count comments for a tweet
    long countByTweet_Id(Long tweetId);
    
    // Count replies for a comment
    long countByParentComment_Id(Long parentCommentId);

    @Query("SELECT c FROM Comment c WHERE c.tweet.id = :tweetId AND c.parentComment IS NULL ORDER BY c.createdAt DESC")
    Page<Comment> findTopLevelCommentsByTweetIdOrderByCreatedAtDesc(@Param("tweetId") Long tweetId, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentCommentId ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentCommentIdOrderByCreatedAtAsc(@Param("parentCommentId") Long parentCommentId);
    
    boolean existsByUser_IdAndTweet_Id(Long userId, Long tweetId);
    
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    Page<Comment> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);
}