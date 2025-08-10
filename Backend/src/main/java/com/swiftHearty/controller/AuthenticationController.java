package com.swiftHearty.controller;

import com.swiftHearty.data.model.User;
import com.swiftHearty.dto.request.*;
import com.swiftHearty.dto.response.CreateNewUserResponse;
import com.swiftHearty.dto.response.GeneralResponse;
import com.swiftHearty.dto.response.OtpResponse;
import com.swiftHearty.services.AdminService;
import com.swiftHearty.services.TokenBlacklistService;
import com.swiftHearty.services.UserService;
import com.swiftHearty.services.VerificationOtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final VerificationOtpService verificationOtpService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AdminService adminService;

    @PostMapping("/register/send-otp")
    public ResponseEntity<?> initiateUserCreationAndSendOtp(@RequestBody CreateUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok("OTP sent to " + request.getPhoneNumber());
    }

    @PostMapping("/verify-otp/complete_registration")
    public ResponseEntity<?> verifyOtpAndRegisterUser(@RequestBody @Valid VerifyOtpRequest request) {
        CreateNewUserResponse response = userService.completeRegistration(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody UserLoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PutMapping("/request-otp/{phoneNumber}")
    public ResponseEntity<OtpResponse> requestOtpCode(@PathVariable("phoneNumber") String phoneNumber){
        verificationOtpService.generateAndSendOtp(phoneNumber);
        return ResponseEntity.ok(
                OtpResponse.builder()
                        .message(String.format("OTP sent successfully to %s " , phoneNumber))
                        .success(true)
                        .build()
        );

    }

    @PostMapping("/request-phone-change-otp")
    public ResponseEntity<GeneralResponse> requestPhoneChangeOtp(@RequestBody RequestPhoneChangeOtp request) {
        return ResponseEntity.ok(userService.requestPhoneChangeOtpForAuthUser(request));

    }

    @PutMapping("/change-phone-number")
    public ResponseEntity<GeneralResponse> changePhoneNumber(@RequestBody ChangePhoneNumberRequest request,
                                                    @AuthenticationPrincipal User currentUser) {
        String currentUserId = currentUser.getId();
        return ResponseEntity.ok(userService.changePhoneNumber(currentUserId,request));
    }

    @PutMapping("/change-password")
    public ResponseEntity<GeneralResponse> changePassword(@RequestBody ChangePasswordRequest request,
                                                             @AuthenticationPrincipal User currentUser) {
        String currentUserId = currentUser.getId();
        return ResponseEntity.ok(userService.changePassword(currentUserId,request));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklist(token);
        }
        return ResponseEntity.ok("Logged out successfully.");
    }

}

