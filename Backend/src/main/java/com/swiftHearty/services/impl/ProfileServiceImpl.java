package com.swiftHearty.services.impl;

import com.swiftHearty.data.model.SecurityProfile;
import com.swiftHearty.data.model.TenantProfile;
import com.swiftHearty.data.repository.SecurityProfileRepository;
import com.swiftHearty.data.repository.TenantProfileRepository;
import com.swiftHearty.dto.request.UpdateSecurityProfileRequest;
import com.swiftHearty.dto.request.UpdateTenantProfileRequest;
import com.swiftHearty.dto.response.GeneralResponse;
import com.swiftHearty.services.ProfileService;
import com.swiftHearty.utils.mappers.ProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final TenantProfileRepository tenantProfileRepository;
    private final SecurityProfileRepository securityProfileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public GeneralResponse updateTenantProfile(String userId, UpdateTenantProfileRequest request) {
        TenantProfile profile = tenantProfileRepository.findTenantByUserId(userId)
                .orElseThrow(() -> new RuntimeException("TenantProfile not found"));

        profileMapper.updateTenantProfileFromDto(request, profile);
        tenantProfileRepository.save(profile);

        return new GeneralResponse("Profile Updated Successfully", true);
    }

    @Override
    public GeneralResponse updateSecurityProfile(String userId, UpdateSecurityProfileRequest request) {
        SecurityProfile profile = securityProfileRepository.findSecurityProfileByUserId(userId)
                .orElseThrow(() -> new RuntimeException("SecurityProfile not found"));

        profileMapper.updateSecurityProfileFromDto(request, profile);
        securityProfileRepository.save(profile);

        return new GeneralResponse("Profile Updated Successfully", true);
    }
}
