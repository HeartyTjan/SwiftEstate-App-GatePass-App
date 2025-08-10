package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.TenantProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TenantProfileRepository extends MongoRepository<TenantProfile,String> {
    Optional<TenantProfile> findTenantByUserId(String id);

}
