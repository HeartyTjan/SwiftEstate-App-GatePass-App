package com.swiftHearty.services.impl;

import com.swiftHearty.data.model.AdminProfile;
import com.swiftHearty.data.model.SecurityProfile;
import com.swiftHearty.data.model.User;
import com.swiftHearty.data.model.WhiteList;
import com.swiftHearty.data.repository.SecurityProfileRepository;
import com.swiftHearty.data.repository.UserRepository;
import com.swiftHearty.data.repository.WhiteListRepository;
import com.swiftHearty.dto.request.AddWhiteListRequest;
import com.swiftHearty.dto.request.CreateSecurityRequest;
import com.swiftHearty.dto.response.CreateNewUserResponse;
import com.swiftHearty.dto.response.WhiteListResponse;
import com.swiftHearty.exception.ResourceAllReadyExistException;
import com.swiftHearty.services.AdminService;
import com.swiftHearty.services.EmailService;
import com.swiftHearty.services.SmsService;
import com.swiftHearty.utils.mappers.UserMapper;
import com.swiftHearty.utils.mappers.WhiteListMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final WhiteListRepository whiteListRepository;
    private final WhiteListMapper whiteListMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityProfileRepository securityProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsService smsService;
    private final EmailService emailService;


    @Override
    public WhiteListResponse addUserToWhiteList(AddWhiteListRequest request){
        if(whiteListRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new ResourceAllReadyExistException("Phone number already exists");
        }

        WhiteList whiteList = whiteListMapper.requestToWhiteList(request);
        whiteListRepository.save(whiteList);

        return whiteListMapper.whiteListToResponse(whiteList);

    }

    @Override
    public boolean existBy(String phoneNumber){
        return whiteListRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    @Transactional
    public CreateNewUserResponse createSecurity(CreateSecurityRequest request) {
        if (!"SECURITY".equalsIgnoreCase(request.getRole())) {
            throw new IllegalArgumentException("Role must be SECURITY");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new DuplicateKeyException("User already exists");
        }
        String randomPassword = generateRandomPassword(12);

        User user = saveUserAndProfile(request,randomPassword);
        userRepository.save(user);

//        String smsMessage = String.format(
//                "Your account has been created successfully.\nUsername: %s\nTemporary Password: %s\nPlease log in and change your password immediately for security purposes.",
//                user.getPhoneNumber(), randomPassword
//        );        smsService.sendSms(user.getPhoneNumber(), smsMessage);

        String subject = "Your Account Has Been Created - Swift Estate";
        String content = String.format(
                """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px; border-radius: 8px;">
                        <h2 style="color: #5c67f2;">Welcome to Swift Estate</h2>
                        
                        <p>Dear User,</p>
                        
                        <p>Your account has been created successfully.</p>
                        
                        <div style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <p><strong>Username:</strong> %s</p>
                            <p><strong>Temporary Password:</strong> %s</p>
                        </div>
                        
                        <p><strong>Important:</strong> Please log in and change your password immediately to ensure the security of your account.</p>
                        
                        <p>If you have any questions or need assistance, feel free to contact our support team.</p>
                        
                        <p style="margin-top: 30px;">Best regards,<br><strong>The Estate Team</strong></p>
                    </div>
                </body>
                </html>
                """,
                user.getPhoneNumber(), randomPassword
        );

        emailService.sendEmail(user.getProfile().getEmail(), subject,content);

        return userMapper.userToResponse(user);
    }

    private User saveUserAndProfile(CreateSecurityRequest request, String randomPassword) {

        User user = userMapper.requestToUser(request, passwordEncoder, randomPassword);
        User savedUser = userRepository.save(user);

        if ("SECURITY".equalsIgnoreCase(user.getRole())) {
            SecurityProfile profile = new SecurityProfile();
            profile.setUserId(savedUser.getId());
            profile.setEmail(request.getEmail());
            securityProfileRepository.save(profile);
            savedUser.setProfile(profile);
        }
        return savedUser;
    }

    private String generateRandomPassword(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    @Override
    public void removeAdminById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        String role = user.getRole();

        if ("SUPER_ADMIN".equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("Cannot delete a SUPER_ADMIN user");
        }

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("User is not an admin and cannot be deleted");
        }

        userRepository.deleteById(id);
    }

    @Override
    public void createAdminWithRandomPassword(String email, String phoneNumber) {
        if (userRepository.findByProfileEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        String randomPassword = java.util.UUID.randomUUID().toString().substring(0, 8);

        AdminProfile adminProfile = new AdminProfile();
        adminProfile.setEmail(email);
        adminProfile.setFullName("Admin");

        User admin = User.builder()
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(randomPassword))
                .role("ADMIN")
                .enabled(true)
                .profile(adminProfile)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        // Email content
        String subject = "Admin Account Created - Swift Estate";
        String content = String.format(
                """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: auto; border: 1px solid #eee; padding: 20px; border-radius: 8px;">
                        <h2 style="color: #5c67f2;">Admin Account Created</h2>
                        
                        <p>Dear Admin,</p>
                        
                        <p>Your admin account has been created for <strong>Swift Estate</strong>.</p>
                        
                        <div style="background-color: #f9f9f9; padding: 15px; border-radius: 5px; margin: 20px 0;">
                            <h3 style="margin-top: 0;">Your Login Credentials:</h3>
                            <p><strong>Username:</strong> %s</p>
                            <p><strong>Password:</strong> %s</p>
                        </div>
                        
                        <p><strong>Important:</strong> Please change your password after your first login for security.</p>
                        
                        <p>You can access the admin portal at: <a href="%s/admin">%s/admin</a></p>
                        
                        <p style="margin-top: 30px;">Best regards,<br><strong>The 3KS&T Team</strong></p>
                    </div>
                </body>
                </html>
                """,
                phoneNumber,
                randomPassword
//                frontendUrl,
//                frontendUrl
        );

        emailService.sendEmail(email, subject, content);
    }


}
