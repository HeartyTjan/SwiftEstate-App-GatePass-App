package com.swiftHearty.data.repository;

import com.swiftHearty.data.model.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String > {
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByProfileEmail(@NotBlank String email);

    long countByRole(String role);

}
