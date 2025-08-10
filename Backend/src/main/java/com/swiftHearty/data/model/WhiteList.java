package com.swiftHearty.data.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "whitelists")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhiteList {

    @Id
    private String id;

    @Email
    @NotBlank
    private String email;

    @Field("phone")
    @Indexed(unique = true)
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String role;

    @NotBlank
    private String estateName;

    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();
}
