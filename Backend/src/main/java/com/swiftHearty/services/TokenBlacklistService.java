package com.swiftHearty.services;

import com.swiftHearty.data.model.TokenBlacklist;
import com.swiftHearty.data.repository.TokenBlacklistRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Value("${jwt_secret}")
    private String jwtSecret;

    public void blacklist(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();

            Date expiration = claims.getExpiration();

            TokenBlacklist blacklistedToken = new TokenBlacklist();
            blacklistedToken.setToken(token);
            blacklistedToken.setExpiryDate(expiration.toInstant());

            tokenBlacklistRepository.save(blacklistedToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public boolean isBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void removeExpiredTokens() {
        Instant now = Instant.now();
        tokenBlacklistRepository.deleteAllByExpiryDateBefore(now);
    }
}
