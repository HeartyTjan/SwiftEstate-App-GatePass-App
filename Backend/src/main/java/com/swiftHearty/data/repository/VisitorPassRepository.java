package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.VisitorPass;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface VisitorPassRepository extends MongoRepository<VisitorPass, String> {
    Optional<VisitorPass> findVisitorPassById(String passId);

}
