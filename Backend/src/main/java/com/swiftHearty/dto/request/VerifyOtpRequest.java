package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {
    @NotBlank
    private String contact;

    private String newPhoneNumber;

    @NotBlank
    private String code;
}
