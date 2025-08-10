package com.swiftHearty.services;

import com.swiftHearty.dto.request.AddWhiteListRequest;
import com.swiftHearty.dto.request.CreateSecurityRequest;
import com.swiftHearty.dto.response.CreateNewUserResponse;
import com.swiftHearty.dto.response.WhiteListResponse;

public interface AdminService {
    WhiteListResponse addUserToWhiteList(AddWhiteListRequest request);

    boolean existBy(String phoneNumber);

    CreateNewUserResponse createSecurity(CreateSecurityRequest request);

    void removeAdminById(String id);

    void createAdminWithRandomPassword(String email, String phoneNumber);
}
