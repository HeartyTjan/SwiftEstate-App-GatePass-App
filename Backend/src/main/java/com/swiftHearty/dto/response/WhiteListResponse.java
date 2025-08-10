package com.swiftHearty.dto.response;

import lombok.Data;

@Data
public class WhiteListResponse {
    private String email;
    private String phoneNumber;
    private String role;
    private String estateName;

}
