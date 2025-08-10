package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.WhiteList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WhiteListRepository extends MongoRepository<WhiteList, String> {

    boolean existsByPhoneNumber( String phoneNumber);

    boolean findByPhoneNumber(String phoneNumber);
}
