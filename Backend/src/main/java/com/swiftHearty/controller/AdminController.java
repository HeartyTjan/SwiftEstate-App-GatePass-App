package com.swiftHearty.controller;

import com.swiftHearty.dto.request.AddWhiteListRequest;
import com.swiftHearty.dto.request.CreateAdminRequest;
import com.swiftHearty.dto.request.CreateSecurityRequest;
import com.swiftHearty.dto.response.WhiteListResponse;
import com.swiftHearty.services.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/add-whitelists")
    public ResponseEntity<WhiteListResponse> addToWhiteList(@Valid @RequestBody AddWhiteListRequest request) {
        WhiteListResponse response = adminService.addUserToWhiteList(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/create-security")
    public ResponseEntity<?> createNewSecurity(@Valid @RequestBody CreateSecurityRequest request) {
        return ResponseEntity.ok(adminService.createSecurity(request));

    }

    @PostMapping("/create-admin")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> createAdmin(@Valid @RequestBody CreateAdminRequest request) {
        adminService.createAdminWithRandomPassword(request.getEmail(), request.getPhoneNumber());
        return ResponseEntity.ok("Admin account created and credentials emailed.");
    }


    @DeleteMapping("/remove-admin/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> removeAdmin(@PathVariable("id") String id) {
        adminService.removeAdminById(id);
        return ResponseEntity.ok("Admin user removed successfully.");
    }


}

