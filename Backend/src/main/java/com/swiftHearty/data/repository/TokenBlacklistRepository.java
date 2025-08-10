package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.TokenBlacklist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TokenBlacklistRepository extends MongoRepository<TokenBlacklist, String> {
    Optional<TokenBlacklist> findByToken(String token);
    boolean existsByToken(String token);

    void deleteAllByExpiryDateBefore(Instant now);
}