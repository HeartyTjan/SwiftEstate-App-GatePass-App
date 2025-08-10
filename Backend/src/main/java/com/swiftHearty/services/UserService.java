package com.swiftHearty.services;

import com.swiftHearty.dto.request.*;
import com.swiftHearty.dto.response.CreateNewUserResponse;
import com.swiftHearty.dto.response.GeneralResponse;
import com.swiftHearty.dto.response.UserLoginResponse;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    @Transactional
    void createUser(CreateUserRequest userRequest);

    CreateNewUserResponse completeRegistration(VerifyOtpRequest request);

    GeneralResponse changePassword(String userID, ChangePasswordRequest request);

    GeneralResponse changePhoneNumber(String userId, ChangePhoneNumberRequest request);

    UserLoginResponse login(UserLoginRequest request);

    GeneralResponse requestPhoneChangeOtpForAuthUser(RequestPhoneChangeOtp request);

    GeneralResponse sendEmailOtpForPhoneRecovery(RecoveryOtpRequest request);

    GeneralResponse changePhoneNumberAfterOtp(VerifyOtpRequest request);

    void sendPasswordResetToken(String email);

    GeneralResponse resetPassword(ResetPasswordRequest request);

    void createSuperAdminIfNonAvailable(String phoneNumber, String password);
}
