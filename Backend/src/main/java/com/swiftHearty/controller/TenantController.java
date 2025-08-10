package com.swiftHearty.controller;

import com.swiftHearty.data.model.User;
import com.swiftHearty.dto.request.AccessCodeRequest;
import com.swiftHearty.dto.request.UpdateTenantProfileRequest;
import com.swiftHearty.dto.response.AccessCodeResponse;
import com.swiftHearty.dto.response.GeneralResponse;
import com.swiftHearty.services.AccessCodeService;
import com.swiftHearty.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TENANT')")
public class TenantController {

    private final AccessCodeService accessCodeService;
    private final ProfileService profileService;

    @PostMapping("/generate")
    public ResponseEntity<AccessCodeResponse> generateAccessCode(@RequestBody AccessCodeRequest request, @AuthenticationPrincipal User user) {
        String userId = user.getId();
        String otp = accessCodeService.generateAccessCode(userId, request);
        if(otp != null) {
            AccessCodeResponse response = new AccessCodeResponse();
            response.setAccessCode(otp);
            response.setSuccess(Boolean.TRUE);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<GeneralResponse> updateProfile(@RequestBody UpdateTenantProfileRequest request,
                                                         @AuthenticationPrincipal User currentUser) {
        String userId = currentUser.getId();
        return ResponseEntity.ok(profileService.updateTenantProfile(userId, request));
    }
}
