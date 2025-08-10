package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePhoneNumberRequest {

    @NotBlank(message = "Phone number is required")
    private String newPhoneNumber;

    @NotBlank(message = "OTP is required")
    private String otp;
}

