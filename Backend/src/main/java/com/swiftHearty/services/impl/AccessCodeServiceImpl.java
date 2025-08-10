package com.swiftHearty.services.impl;

import com.swiftHearty.data.model.AccessCode;
import com.swiftHearty.data.model.SecurityProfile;
import com.swiftHearty.data.model.TenantProfile;
import com.swiftHearty.data.model.VisitorPass;
import com.swiftHearty.data.repository.AccessCodeRepository;
import com.swiftHearty.data.repository.SecurityProfileRepository;
import com.swiftHearty.data.repository.TenantProfileRepository;
import com.swiftHearty.dto.request.AccessCodeRequest;
import com.swiftHearty.dto.request.ValidateAccessCodeRequest;
import com.swiftHearty.dto.response.AccessCodeResponse;
import com.swiftHearty.exception.AccessCodeAlreadyUsedException;
import com.swiftHearty.exception.ResourceNotFoundException;
import com.swiftHearty.services.AccessCodeService;
import com.swiftHearty.services.VisitorPassService;
import com.swiftHearty.utils.mappers.AccessCodeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AccessCodeServiceImpl implements AccessCodeService {
    private static final Logger log = Logger.getLogger(AccessCodeServiceImpl.class.getName());
    private final AccessCodeRepository accessCodeRepository;
    private final VisitorPassService visitorPassService;
    private final AccessCodeMapper accessCodeMapper;
    private final TenantProfileRepository tenantProfileRepository;
    private final SecurityProfileRepository securityProfileRepository;

    @Value("${accesscode.expiration.minutes}")
    private int expirationMinutes;

    private final SecureRandom secureRandom = new SecureRandom();

    @Scheduled(fixedRate = 600000)
    @Override
    public void deleteExpiredAccessCode() {
        LocalDateTime currentTime = LocalDateTime.now();

        try {
            int deletedCount = accessCodeRepository.deleteByExpirationTimeBefore(currentTime);
            if (deletedCount > 0) {
                log.log(Level.INFO, "Deleted expired access codes. {0}", deletedCount);
            } else {
                log.info("No expired access codes found to delete.");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE,"Failed to delete expired access codes", e);
        }
    }

    @Scheduled(cron = "0 0 3 * * ?") // every day at 3 AM
    public void deleteOldUsedAccessCodes() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        int deletedCount = accessCodeRepository.deleteByUsedTrueAndExpirationTimeBefore(cutoffDate);
        log.log(Level.INFO,"Deleted {} used and old access codes.", deletedCount);
    }

    @Override
    public String generateAccessCode(String userId, AccessCodeRequest request) {
        validateUserProfileComplete(userId);

        String accessCode = generateUniqueAccessCode();

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(expirationMinutes);
        AccessCode accessCodeRecord = createAccessCodeRecord(request, accessCode, expirationTime);
        accessCodeRepository.save(accessCodeRecord);
        return accessCode;
    }

    @Transactional
    @Override
    public AccessCodeResponse retrieveAccessCodeRecordByKey(String userId, ValidateAccessCodeRequest request) {
        validateUserProfileComplete(userId);

        AccessCode foundAccessCode = accessCodeRepository.getAccessCodeBy(request.getOtpCode())
                .orElseThrow(() -> new ResourceNotFoundException("Expired or Invalid AccessCode"));

        if (foundAccessCode.isUsed()) {
            throw new AccessCodeAlreadyUsedException("AccessCode already used");
        }

        if (foundAccessCode.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Expired AccessCode");
        }

        foundAccessCode.setUsed(true);
        accessCodeRepository.save(foundAccessCode);

        VisitorPass visitorPass = visitorPassService.generateVisitorPass(foundAccessCode, request.getSecurityName());
        return accessCodeMapper.mapToResponse(foundAccessCode.getCode(), visitorPass);
    }

    @Override
    public Long numberOfAccessCode() {
        return accessCodeRepository.count();
    }

    private AccessCode createAccessCodeRecord(AccessCodeRequest request, String OTP, LocalDateTime expirationTime) {
        AccessCode accessCodeRecord = new AccessCode();
        String fullName = String.join(" ", request.getFirstName(), request.getLastName());

        accessCodeRecord.setUserFullName(fullName);
        accessCodeRecord.setUsed(false);
        accessCodeRecord.setVisitorName(request.getVisitorName());
        accessCodeRecord.setApartmentId(request.getApartmentId());
        accessCodeRecord.setCode(OTP);
        accessCodeRecord.setExpirationTime(expirationTime);
        accessCodeRecord.setCreatedTime(LocalDateTime.now());
        accessCodeRecord.setUserPhoneNumber(request.getPhoneNumber());

        return accessCodeRecord;
    }

    private String generateRandomOtp() {
        int otp = 100_000 + secureRandom.nextInt(900_000);
        return String.valueOf(otp);
    }

    private String generateUniqueAccessCode() {
        String code;
        do {
            code = generateRandomOtp();
        } while (accessCodeRepository.existsAccessCodeBy(code));
        return code;
    }

//    public void triggerOTPCleanup() {
//        new ExpiredOTPCleanUp(otpRepository).run();
//    }


    private void validateUserProfileComplete(String userId) {
        TenantProfile tenantProfile = tenantProfileRepository.findTenantByUserId(userId).orElse(null);

        if (tenantProfile != null) {
            if (isBlank(tenantProfile.getFirstName()) ||
                    isBlank(tenantProfile.getLastName()) ||
                    isBlank(tenantProfile.getApartmentNumber())) {
                throw new IllegalStateException("Tenant profile must be complete (first name, last name, apartment number) before generating access code.");
            }
            return;
        }

        SecurityProfile securityProfile = securityProfileRepository.findSecurityProfileByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Security profile not found."));

        if (isBlank(securityProfile.getFirstName()) ||
                isBlank(securityProfile.getLastName())){
            throw new IllegalStateException("Security profile must be complete (first name, last name) before validating access code.");
        }
    }
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}



