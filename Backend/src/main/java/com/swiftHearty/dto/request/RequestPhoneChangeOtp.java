package com.swiftHearty.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestPhoneChangeOtp {
    @NotBlank(message = "Phone number is required")
    private String newPhoneNumber;}
