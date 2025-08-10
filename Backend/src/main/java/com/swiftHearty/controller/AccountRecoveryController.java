package com.swiftHearty.controller;

import com.swiftHearty.dto.request.ForgotPasswordRequest;
import com.swiftHearty.dto.request.RecoveryOtpRequest;
import com.swiftHearty.dto.request.ResetPasswordRequest;
import com.swiftHearty.dto.request.VerifyOtpRequest;
import com.swiftHearty.dto.response.GeneralResponse;
import com.swiftHearty.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/account-recovery")
@RequiredArgsConstructor
public class AccountRecoveryController {

    private final UserService userService;

    @PostMapping("/request-recovery-otp")
    public ResponseEntity<GeneralResponse> requestRecoveryOtp(@Valid @RequestBody RecoveryOtpRequest request) {
        GeneralResponse response = userService.sendEmailOtpForPhoneRecovery(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-phone-number")
    public ResponseEntity<GeneralResponse> changePhoneNumberAfterOtp(@Valid @RequestBody VerifyOtpRequest request) {
        GeneralResponse response = userService.changePhoneNumberAfterOtp(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GeneralResponse> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        userService.sendPasswordResetToken(request.getEmail());
        return ResponseEntity.ok(new GeneralResponse("Password reset email sent if email exists", true));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GeneralResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(userService.resetPassword(request));
    }


}
