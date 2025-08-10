package com.swiftHearty.services;

import com.swiftHearty.dto.request.UpdateSecurityProfileRequest;
import com.swiftHearty.dto.request.UpdateTenantProfileRequest;
import com.swiftHearty.dto.response.GeneralResponse;

public interface ProfileService {

    GeneralResponse updateTenantProfile(String userId, UpdateTenantProfileRequest request);

    GeneralResponse updateSecurityProfile(String userId, UpdateSecurityProfileRequest request);
}
