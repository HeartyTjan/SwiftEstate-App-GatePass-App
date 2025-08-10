package com.swiftHearty.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "token_blacklist")
public class TokenBlacklist {

    @Id
    private String id;

    private String token;

    private Instant expiryDate;
}
