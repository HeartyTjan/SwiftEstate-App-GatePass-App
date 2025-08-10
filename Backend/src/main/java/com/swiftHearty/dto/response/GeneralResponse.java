package com.swiftHearty.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GeneralResponse {
    private String message;
    private boolean success;
}

