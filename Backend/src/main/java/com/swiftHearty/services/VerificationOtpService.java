package com.swiftHearty.services;

import com.swiftHearty.dto.request.CreateUserRequest;

public interface VerificationOtpService {

    void generateAndSendOtp(CreateUserRequest request);

    void generateAndSendEmailOtp(String email, String recipientName);

    void generateAndSendOtp(String phoneNumber);

    void verifyOtp(String phoneNumber, String inputOtp);

    void storePendingUserRequest(CreateUserRequest request);

    CreateUserRequest getPendingUserRequest(String phoneNumber);
}
