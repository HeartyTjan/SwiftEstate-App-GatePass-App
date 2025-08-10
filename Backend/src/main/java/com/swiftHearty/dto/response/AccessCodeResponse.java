package com.swiftHearty.dto.response;

import com.swiftHearty.data.model.VisitorPass;
import lombok.Data;

@Data
public class AccessCodeResponse {
    private String accessCode;
    private boolean success;
    private VisitorPass visitorPass;
}
