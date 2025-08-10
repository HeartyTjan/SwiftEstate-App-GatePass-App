package com.swiftHearty.services;

import com.swiftHearty.data.model.AccessCode;
import com.swiftHearty.data.model.TenantProfile;
import com.swiftHearty.data.model.VisitorPass;
import com.swiftHearty.data.repository.AccessCodeRepository;
import com.swiftHearty.data.repository.TenantProfileRepository;
import com.swiftHearty.dto.request.AccessCodeRequest;
import com.swiftHearty.dto.request.ValidateAccessCodeRequest;
import com.swiftHearty.dto.response.AccessCodeResponse;
import com.swiftHearty.exception.AccessCodeAlreadyUsedException;
import com.swiftHearty.exception.ResourceNotFoundException;
import com.swiftHearty.services.impl.AccessCodeServiceImpl;
import com.swiftHearty.utils.mappers.AccessCodeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessCodeServiceImplTest {

    @Mock
    private AccessCodeRepository accessCodeRepository;

    @Mock
    private TenantProfileRepository tenantProfileRepository;
    @Mock
    private VisitorPassService visitorPassService;

    @Mock
    private AccessCodeMapper accessCodeMapper;

    @InjectMocks
    private AccessCodeServiceImpl accessCodeService;

    private AccessCodeRequest accessCodeRequest;
    private ValidateAccessCodeRequest validateRequest;
    private AccessCode accessCode;
    private VisitorPass visitorPass;
    private AccessCodeResponse accessCodeResponse;

    @BeforeEach
    void setUp() {
        accessCodeRequest = new AccessCodeRequest();
        accessCodeRequest.setFirstName("John");
        accessCodeRequest.setLastName("Doe");
        accessCodeRequest.setVisitorName("Jane Doe");
        accessCodeRequest.setApartmentId("A101");
        accessCodeRequest.setPhoneNumber("1234567890");

        validateRequest = new ValidateAccessCodeRequest();
        validateRequest.setOtpCode("123456");
        validateRequest.setSecurityName("SecurityGuard");

        accessCode = new AccessCode();
        accessCode.setCode("123456");
        accessCode.setUsed(false);
        accessCode.setExpirationTime(LocalDateTime.now().plusMinutes(30));
        accessCode.setUserFullName("John Doe");
        accessCode.setVisitorName("Jane Doe");
        accessCode.setApartmentId("A101");
        accessCode.setUserPhoneNumber("1234567890");

        visitorPass = new VisitorPass();
        visitorPass.setId("1L");

        accessCodeResponse = new AccessCodeResponse();
        accessCodeResponse.setAccessCode("123456");
        accessCodeResponse.setVisitorPass(visitorPass);
    }

    @Test
    @DisplayName("Should generate a 6-digit access code and store it")
    void testGenerateAccessCode() {
        TenantProfile mockProfile = new TenantProfile();
        mockProfile.setFirstName("John");
        mockProfile.setLastName("Doe");
        mockProfile.setApartmentNumber("APT10");

        when(tenantProfileRepository.findTenantByUserId(anyString())).thenReturn(Optional.of(mockProfile));
        when(accessCodeRepository.save(any(AccessCode.class))).thenReturn(accessCode);

        String generatedCode = accessCodeService.generateAccessCode(anyString(), accessCodeRequest);

        assertNotNull(generatedCode);
        assertEquals(6, generatedCode.length());
        verify(accessCodeRepository, times(1)).save(any(AccessCode.class));
    }

    @Test
    @DisplayName("Should retrieve valid access code and generate visitor pass")
    void testRetrieveAccessCodeRecordByKey_ValidCode() {
        when(accessCodeRepository.getAccessCodeBy("123456")).thenReturn(Optional.of(accessCode));
        when(visitorPassService.generateVisitorPass(accessCode, "SecurityGuard")).thenReturn(visitorPass);
        when(accessCodeMapper.mapToResponse("123456", visitorPass)).thenReturn(accessCodeResponse);

        AccessCodeResponse response = accessCodeService.retrieveAccessCodeRecordByKey(anyString(),validateRequest);

        assertNotNull(response);
        assertEquals("123456", response.getAccessCode());
        assertEquals(visitorPass.getId(), response.getVisitorPass().getId());
        verify(accessCodeRepository, times(1)).save(accessCode);
        assertTrue(accessCode.isUsed());
    }

    @Test
    @DisplayName("Should throw AccessCodeAlreadyUsedException if code has already been used")
    void testRetrieveAccessCodeRecordByKey_AlreadyUsed() {
        accessCode.setUsed(true);
        when(accessCodeRepository.getAccessCodeBy("123456")).thenReturn(Optional.of(accessCode));

        assertThrows(AccessCodeAlreadyUsedException.class, () ->
                accessCodeService.retrieveAccessCodeRecordByKey(anyString(),validateRequest));
        verify(accessCodeRepository, never()).save(any(AccessCode.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if access code is expired")
    void testRetrieveAccessCodeRecordByKey_ExpiredCode() {
        accessCode.setExpirationTime(LocalDateTime.now().minusMinutes(30));
        when(accessCodeRepository.getAccessCodeBy("123456")).thenReturn(Optional.of(accessCode));

        assertThrows(ResourceNotFoundException.class, () ->
                accessCodeService.retrieveAccessCodeRecordByKey(anyString(), validateRequest));
        verify(accessCodeRepository, never()).save(any(AccessCode.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException if access code is not found")
    void testRetrieveAccessCodeRecordByKey_NotFound() {
        when(accessCodeRepository.getAccessCodeBy("123456")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                accessCodeService.retrieveAccessCodeRecordByKey(anyString(),validateRequest));
        verify(accessCodeRepository, never()).save(any(AccessCode.class));
    }

    @Test
    @DisplayName("Should delete all expired access codes")
    void testDeleteExpiredAccessCode() {
        LocalDateTime pastTime = LocalDateTime.now().minusMinutes(31);
        AccessCode expiredCode = new AccessCode();
        expiredCode.setExpirationTime(pastTime);
        when(accessCodeRepository.findAccessCodeByExpirationTimeBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(expiredCode));

        accessCodeService.deleteExpiredAccessCode();

        verify(accessCodeRepository, times(1)).findAccessCodeByExpirationTimeBefore(any(LocalDateTime.class));
        verify(accessCodeRepository, times(1)).deleteAll(anyList());
    }

    @Test
    @DisplayName("Should return total number of access codes")
    void testNumberOfAccessCode() {
        when(accessCodeRepository.count()).thenReturn(5L);

        Long count = accessCodeService.numberOfAccessCode();

        assertEquals(5L, count);
        verify(accessCodeRepository, times(1)).count();
    }
}
