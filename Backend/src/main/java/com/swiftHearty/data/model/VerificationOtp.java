package com.swiftHearty.data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@AllArgsConstructor
public class VerificationOtp {
    private String code;
    private LocalDateTime expiresAt;
}
