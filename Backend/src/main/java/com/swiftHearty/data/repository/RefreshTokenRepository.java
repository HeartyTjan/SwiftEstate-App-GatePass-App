package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    void deleteByUserId(String userId);

    Optional<RefreshToken> findByToken(String token);
}
