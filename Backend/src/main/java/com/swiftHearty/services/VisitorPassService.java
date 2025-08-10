package com.swiftHearty.services;

import com.swiftHearty.data.model.AccessCode;
import com.swiftHearty.data.model.VisitorPass;
import com.swiftHearty.dto.response.VisitorPassResponse;

public interface VisitorPassService {
    VisitorPass generateVisitorPass(AccessCode otp, String securityName);
    VisitorPassResponse retrieveVisitorPass(String passId);

    VisitorPassResponse closeVisitorPass(String id);
}
