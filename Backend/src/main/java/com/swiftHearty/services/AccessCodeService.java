package com.swiftHearty.services;

import com.swiftHearty.dto.request.AccessCodeRequest;
import com.swiftHearty.dto.request.ValidateAccessCodeRequest;
import com.swiftHearty.dto.response.AccessCodeResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

public interface AccessCodeService {

    @Scheduled(fixedRate = 600000)
    void deleteExpiredAccessCode();

    String generateAccessCode(String userId, AccessCodeRequest request);

    @Transactional
    AccessCodeResponse retrieveAccessCodeRecordByKey(String userId, ValidateAccessCodeRequest request);

    Long numberOfAccessCode();
}
