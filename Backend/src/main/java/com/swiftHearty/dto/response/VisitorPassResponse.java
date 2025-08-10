package com.swiftHearty.dto.response;

import com.swiftHearty.data.model.VisitorPass;
import lombok.Data;

@Data
public class VisitorPassResponse {
    private VisitorPass visitorPass;
    private boolean success;
}
