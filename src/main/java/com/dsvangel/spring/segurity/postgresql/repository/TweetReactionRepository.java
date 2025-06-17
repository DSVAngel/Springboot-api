package com.dsvangel.spring.segurity.postgresql.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dsvangel.spring.segurity.postgresql.models.TweetReaction;

@Repository
public interface TweetReactionRepository extends JpaRepository<TweetReaction, Long> {
    
    List<TweetReaction> findByTweet_Id(Long tweetId);
    
    List<TweetReaction> findByUser_Id(Long userId);
    
    Optional<TweetReaction> findByUser_IdAndTweet_Id(Long userId, Long tweetId);
    
    long countByTweet_Id(Long tweetId);
    
    long countByTweet_IdAndReaction_Id(Long tweetId, Long reactionId);
    
    boolean existsByUser_IdAndTweet_Id(Long userId, Long tweetId);
    
    @Query("SELECT tr FROM TweetReaction tr WHERE tr.tweet.id = :tweetId AND tr.reaction.id = :reactionId")
    List<TweetReaction> findByTweetIdAndReactionId(@Param("tweetId") Long tweetId, @Param("reactionId") Long reactionId);
    
    @Query("SELECT COUNT(tr) FROM TweetReaction tr WHERE tr.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    void deleteByUser_IdAndTweet_Id(Long userId, Long tweetId);
}