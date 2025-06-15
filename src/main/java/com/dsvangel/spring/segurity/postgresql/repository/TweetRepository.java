package com.dsvangel.spring.segurity.postgresql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsvangel.spring.segurity.postgresql.models.Tweet;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {

}

