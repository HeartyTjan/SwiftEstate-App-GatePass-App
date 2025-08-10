package com.swiftHearty.services.impl;

import com.swiftHearty.data.model.AdminProfile;
import com.swiftHearty.data.model.TenantProfile;
import com.swiftHearty.data.model.User;
import com.swiftHearty.data.repository.SecurityProfileRepository;
import com.swiftHearty.data.repository.TenantProfileRepository;
import com.swiftHearty.data.repository.UserRepository;
import com.swiftHearty.dto.request.*;
import com.swiftHearty.dto.response.CreateNewUserResponse;
import com.swiftHearty.dto.response.GeneralResponse;
import com.swiftHearty.dto.response.UserLoginResponse;
import com.swiftHearty.exception.PhoneNumberNotWhitelistedException;
import com.swiftHearty.exception.ResourceAllReadyExistException;
import com.swiftHearty.services.AdminService;
import com.swiftHearty.services.UserService;
import com.swiftHearty.services.VerificationOtpService;
import com.swiftHearty.utils.jwt.JwtUtil;
import com.swiftHearty.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminService adminService;
    private final VerificationOtpService verificationOtpService;
    private final TenantProfileRepository tenantProfileRepository;
    private final SecurityProfileRepository securityProfileRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${super.admin_name}")
    String fullName;

    @Value("${super.admin_email}")
    String email;



    @Override
    public void createUser(CreateUserRequest userRequest) {
        if (!"TENANT".equalsIgnoreCase(userRequest.getRole())) {
            throw new IllegalArgumentException("Role must be TENANT");
        }
        if (!adminService.existBy(userRequest.getPhoneNumber())) {
            throw new PhoneNumberNotWhitelistedException("Unauthorized Registration, contact Estate Management");
        }
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            throw new DuplicateKeyException("User already exists");
        }

        verificationOtpService.generateAndSendOtp(userRequest);
    }

    @Override
    public CreateNewUserResponse completeRegistration(VerifyOtpRequest request) {

        verificationOtpService.verifyOtp(request.getContact(), request.getCode());
        logger.info("OTP has been verified");


        CreateUserRequest userRequest = verificationOtpService.getPendingUserRequest(request.getContact());
        logger.info("Pending request extracted {}" , userRequest);

        User user = saveUserAndProfile(userRequest);
        logger.info("User exist{}" , user);

        userRepository.save(user);

       return userMapper.userToResponse(user);
    }

    @Override
    public GeneralResponse changePassword(String userID, ChangePasswordRequest request) {
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new RuntimeException("New password must be different from the current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return new GeneralResponse("Password updated Successfully", true);
    }

    @Override
    public GeneralResponse changePhoneNumber(String userId, ChangePhoneNumberRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newPhone = request.getNewPhoneNumber();

        verificationOtpService.verifyOtp(newPhone, request.getOtp());

        user.setPhoneNumber(newPhone);
        userRepository.save(user);
        return new GeneralResponse(String.format("Phone number changed Successfully to %s", newPhone), true);
    }


    @Override
    public UserLoginResponse login(UserLoginRequest request){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getPhoneNumber(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getRole());
            return userMapper.mapToLoginResponse(token, user, "Login Successfully");
    }


    @Override
    public GeneralResponse requestPhoneChangeOtpForAuthUser(RequestPhoneChangeOtp request) {
        String phone = request.getNewPhoneNumber();

        if (userRepository.existsByPhoneNumber(phone)) {
            throw new ResourceAllReadyExistException("Phone number already Taken");
        }

        if (!adminService.existBy(phone)) {
           throw new PhoneNumberNotWhitelistedException("Phone number is not allowed, Contact Estate Admin to update new Phone Number");
        }

        verificationOtpService.generateAndSendOtp(phone);

        return new GeneralResponse(String.format("OTP sent to %s", phone),true);
    }

    @Override
    public GeneralResponse sendEmailOtpForPhoneRecovery(RecoveryOtpRequest request) {
        User user = userRepository.findByProfileEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fullName = user.getProfile().getFullName();
        String email = user.getProfile().getEmail();

        verificationOtpService.generateAndSendEmailOtp(email, fullName);

        return new GeneralResponse(String.format("OTP sent to your email address %s", email), true);
    }

    @Override
    public GeneralResponse changePhoneNumberAfterOtp(VerifyOtpRequest request) {
        User user = userRepository.findByProfileEmail(request.getContact())
                .orElseThrow(() -> new RuntimeException("User not found"));

      verificationOtpService.verifyOtp(request.getContact(), request.getCode());

        if (userRepository.existsByPhoneNumber(request.getNewPhoneNumber())) {
            throw new RuntimeException("Phone number already in use");
        }

        user.setPhoneNumber(request.getNewPhoneNumber());
        userRepository.save(user);

        return new GeneralResponse("Phone number updated successfully.", true);
    }

    @Override
    public void sendPasswordResetToken(String email) {
        User user = userRepository.findByProfileEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fullName = user.getProfile().getFullName();

        verificationOtpService.generateAndSendEmailOtp(email, fullName);
    }

    @Override
    public GeneralResponse resetPassword(ResetPasswordRequest request) {
        verificationOtpService.verifyOtp(request.getEmail(), request.getOtp());

        User user = userRepository.findByProfileEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new GeneralResponse("Password reset successfully", true);
    }

    private User saveUserAndProfile(CreateUserRequest request) {
        User user = userMapper.requestToUser(request, passwordEncoder);
        User savedUser = userRepository.save(user);

        if ("TENANT".equalsIgnoreCase(user.getRole())) {
            TenantProfile profile = new TenantProfile();
            profile.setUserId(savedUser.getId());

            tenantProfileRepository.save(profile);
            savedUser.setProfile(profile);
        }

        return savedUser;
    }

    @Override
    public void createSuperAdminIfNonAvailable(String phoneNumber, String password){
        if (userRepository.countByRole("SUPER_ADMIN") == 0) {

            AdminProfile adminProfile = new AdminProfile();
            adminProfile.setFullName(fullName);
            adminProfile.setEmail(email);

            User admin = User.builder()
                    .phoneNumber(phoneNumber)
                    .password(passwordEncoder.encode(password))
                    .role("SUPER_ADMIN")
                    .createdAt(LocalDateTime.now())
                    .profile(adminProfile)
                    .build();

            userRepository.save(admin);
        }
    }

}
