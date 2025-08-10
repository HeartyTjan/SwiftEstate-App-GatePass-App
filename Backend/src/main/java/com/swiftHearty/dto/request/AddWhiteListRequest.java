package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
public class AddWhiteListRequest {
    @NotBlank
    private String email;

    @Field("phone")
    @Indexed(unique = true)
    @NotBlank
    private String phoneNumber;

    @NotBlank
    private String role;

    private String estateName;

    @NotBlank
    private String createdBy;

}

