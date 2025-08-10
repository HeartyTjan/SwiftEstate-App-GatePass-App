package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.SecurityProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SecurityProfileRepository extends MongoRepository<SecurityProfile, String> {
    Optional<SecurityProfile> findSecurityProfileByUserId(String id);
}
