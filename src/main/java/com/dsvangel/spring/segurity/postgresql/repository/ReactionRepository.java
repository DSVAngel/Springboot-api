package com.dsvangel.spring.segurity.postgresql.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dsvangel.spring.segurity.postgresql.models.EReaction;
import com.dsvangel.spring.segurity.postgresql.models.Reaction;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    
    Optional<Reaction> findByDescription(EReaction description);
    
    boolean existsByDescription(EReaction description);
}