package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ValidateAccessCodeRequest {
    @NotBlank(message = "OTP code is required")
    private String otpCode;

    @NotBlank(message = "Security Name is required")
    private String securityName;
}
