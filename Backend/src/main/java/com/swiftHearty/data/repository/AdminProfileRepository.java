package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.AdminProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdminProfileRepository extends MongoRepository<AdminProfile, String> {
}
