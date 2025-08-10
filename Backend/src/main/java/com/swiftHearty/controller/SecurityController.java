package com.swiftHearty.controller;

import com.swiftHearty.data.model.User;
import com.swiftHearty.dto.request.UpdateSecurityProfileRequest;
import com.swiftHearty.dto.request.ValidateAccessCodeRequest;
import com.swiftHearty.dto.response.AccessCodeResponse;
import com.swiftHearty.dto.response.GeneralResponse;
import com.swiftHearty.dto.response.VisitorPassResponse;
import com.swiftHearty.services.AccessCodeService;
import com.swiftHearty.services.ProfileService;
import com.swiftHearty.services.VisitorPassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/security")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('SECURITY')")
public class SecurityController {


    private final AccessCodeService accessCodeService;

    private final VisitorPassService visitorPassService;

    private final ProfileService profileService;

    @PostMapping("/validate-access-code")
    public ResponseEntity<AccessCodeResponse> validateAccessCode(@Valid  @RequestBody ValidateAccessCodeRequest request,
                                                                 @AuthenticationPrincipal User currentUser){
        String userId = currentUser.getId();
        AccessCodeResponse response =  accessCodeService.retrieveAccessCodeRecordByKey(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/visitor-pass/{id}")
    public ResponseEntity<VisitorPassResponse> retrievePass(@PathVariable("id") String id){
        VisitorPassResponse response =  visitorPassService.retrieveVisitorPass(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/close/visitor-pass/{id}")
    public ResponseEntity<VisitorPassResponse> closePass(@PathVariable("id") String id){
       VisitorPassResponse response = visitorPassService.closeVisitorPass(id);
       return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PutMapping("/update-profile")
    public ResponseEntity<GeneralResponse> updateProfile(@RequestBody UpdateSecurityProfileRequest request,
                                                         @AuthenticationPrincipal User currentUser) {
        String userId = currentUser.getId();
        return ResponseEntity.ok(profileService.updateSecurityProfile(userId, request));
    }
}
