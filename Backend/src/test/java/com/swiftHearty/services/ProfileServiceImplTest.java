package com.swiftHearty.services;

import com.swiftHearty.data.model.SecurityProfile;
import com.swiftHearty.data.model.TenantProfile;
import com.swiftHearty.data.repository.SecurityProfileRepository;
import com.swiftHearty.data.repository.TenantProfileRepository;
import com.swiftHearty.dto.request.UpdateSecurityProfileRequest;
import com.swiftHearty.dto.request.UpdateTenantProfileRequest;
import com.swiftHearty.dto.response.GeneralResponse;
import com.swiftHearty.services.impl.ProfileServiceImpl;
import com.swiftHearty.utils.mappers.ProfileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceImplTest {

    @Mock
    private TenantProfileRepository tenantProfileRepository;

    @Mock
    private SecurityProfileRepository securityProfileRepository;

    @Mock
    private ProfileMapper profileMapper;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private TenantProfile tenantProfile;
    private SecurityProfile securityProfile;
    private UpdateTenantProfileRequest tenantRequest;
    private UpdateSecurityProfileRequest securityRequest;
    private final String userId = "user-123";

    @BeforeEach
    void setUp() {
        tenantProfile = new TenantProfile();
        securityProfile = new SecurityProfile();

        tenantRequest = new UpdateTenantProfileRequest();
        tenantRequest.setFirstName("John");
        tenantRequest.setLastName("Doe");

        securityRequest = new UpdateSecurityProfileRequest();
        securityRequest.setFirstName("Jane");
        securityRequest.setLastName("Smith");
    }

    @Test
    @DisplayName("Should update tenant profile successfully")
    void updateTenantProfile_Success() {
        when(tenantProfileRepository.findTenantByUserId(userId)).thenReturn(Optional.of(tenantProfile));

        GeneralResponse response = profileService.updateTenantProfile(userId, tenantRequest);

        verify(profileMapper).updateTenantProfileFromDto(tenantRequest, tenantProfile);
        verify(tenantProfileRepository).save(tenantProfile);
        assertEquals("Profile Updated Successfully", response.getMessage());
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("Should throw exception when tenant profile not found")
    void updateTenantProfile_ProfileNotFound() {
        when(tenantProfileRepository.findTenantByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                profileService.updateTenantProfile(userId, tenantRequest));

        assertEquals("TenantProfile not found", exception.getMessage());
        verify(tenantProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update security profile successfully")
    void updateSecurityProfile_Success() {
        when(securityProfileRepository.findSecurityProfileByUserId(userId)).thenReturn(Optional.of(securityProfile));

        GeneralResponse response = profileService.updateSecurityProfile(userId, securityRequest);

        verify(profileMapper).updateSecurityProfileFromDto(securityRequest, securityProfile);
        verify(securityProfileRepository).save(securityProfile);
        assertEquals("Profile Updated Successfully", response.getMessage());
        assertTrue(response.isSuccess());
    }

    @Test
    @DisplayName("Should throw exception when security profile not found")
    void updateSecurityProfile_ProfileNotFound() {
        when(securityProfileRepository.findSecurityProfileByUserId(userId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                profileService.updateSecurityProfile(userId, securityRequest));

        assertEquals("SecurityProfile not found", exception.getMessage());
        verify(securityProfileRepository, never()).save(any());
    }
}
