package com.swiftHearty.dto.response;

import lombok.Data;

@Data
public class ExceptionResponse {
    private String message;
    private boolean success;
}
