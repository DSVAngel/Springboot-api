package com.dsvangel.spring.segurity.postgresql.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsvangel.spring.segurity.postgresql.models.Tweet;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    
    // Get all tweets ordered by ID descending (most recent first)
    Page<Tweet> findAllByOrderByIdDesc(Pageable pageable);
    
    // Get tweets by user ordered by ID descending
    Page<Tweet> findByPostedBy_IdOrderByIdDesc(Long userId, Pageable pageable);
    
    // Get tweets by user
    Page<Tweet> findByPostedBy_Id(Long userId, Pageable pageable);
    
    // Count tweets by user
    long countByPostedBy_Id(Long userId);
    
    // Search tweets by content
    @Query("SELECT t FROM Tweet t WHERE t.tweet LIKE %:searchTerm% ORDER BY t.id DESC")
    Page<Tweet> findByTweetContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Get tweets with minimum length
    @Query("SELECT t FROM Tweet t WHERE LENGTH(t.tweet) >= :minLength ORDER BY t.id DESC")
    Page<Tweet> findTweetsWithMinimumLength(@Param("minLength") int minLength, Pageable pageable);
}