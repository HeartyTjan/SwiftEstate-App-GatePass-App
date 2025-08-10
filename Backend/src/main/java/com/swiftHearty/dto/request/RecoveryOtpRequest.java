package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecoveryOtpRequest {
    @NotBlank
    private String email;
}
