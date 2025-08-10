package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.AccessCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessCodeRepository extends MongoRepository<AccessCode,String> {
    Optional<AccessCode>  getAccessCodeBy(String accessCode);

    List<AccessCode> findAccessCodeByExpirationTimeBefore(LocalDateTime currentTime);

    boolean existsAccessCodeBy(String accessCode);

    int deleteByExpirationTimeBefore(LocalDateTime currentTime);

    int deleteByUsedTrueAndExpirationTimeBefore(LocalDateTime cutoffDate);
}
