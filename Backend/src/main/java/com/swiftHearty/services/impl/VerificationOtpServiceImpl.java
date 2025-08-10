package com.swiftHearty.services.impl;

import com.swiftHearty.data.model.VerificationOtp;
import com.swiftHearty.dto.request.CreateUserRequest;
import com.swiftHearty.services.EmailService;
import com.swiftHearty.services.SmsService;
import com.swiftHearty.services.VerificationOtpService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class VerificationOtpServiceImpl implements VerificationOtpService {
    private static final Logger logger = LoggerFactory.getLogger(VerificationOtpServiceImpl.class);
    private final SmsService smsService;
    private final EmailService emailService;

    @Getter
    private final Map<String, VerificationOtp> otpStorage = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, CreateUserRequest> pendingUserRequests = new ConcurrentHashMap<>();


    private final SecureRandom random = new SecureRandom();

    @Override
    public void generateAndSendOtp(CreateUserRequest request) {
            generateOtp(request.getPhoneNumber());

            storePendingUserRequest(request);
            logger.info("Pending user in storage {}", getPendingUserRequest(request.getPhoneNumber()));

    }

    @Override
    public void generateAndSendEmailOtp(String email, String recipientName) {
        String code = String.format("%06d", random.nextInt(1000000));
        String subject = "Email Verification Code - SwiftEstate";

        String content = String.format(
                """
                <html>
                <body style="font-family: Arial, sans-serif; color: #333;">
                    <div style="max-width: 600px; margin: auto; padding: 20px; background-color: #f9f9f9; border-radius: 8px;">
                        <h2 style="color: #4F46E5;">Hello %s,</h2>
        
                        <p>Thank you for registering with <strong>SwiftEstate</strong>.</p>
        
                        <p>Your email verification code is:</p>
                        
                        <p style="font-size: 24px; font-weight: bold; background-color: #e0e7ff; padding: 12px; text-align: center; border-radius: 6px; color: #4F46E5;">
                            %s
                        </p>
        
                        <p>This code will expire in <strong>5 minutes</strong>.</p>
        
                        <p>If you did not initiate this request, please ignore this message.</p>
        
                        <p style="margin-top: 30px;">Best regards,<br><strong>The SwiftEstate Team</strong></p>
                    </div>
                </body>
                </html>
                """,
                recipientName, code
        );

        emailService.sendEmail(email, subject, content);
        otpStorage.put(email, new VerificationOtp(code, LocalDateTime.now().plusMinutes(5)));
    }

    @Override
    public void generateAndSendOtp(String phoneNumber) {
        VerificationOtp existingOtp = otpStorage.get(phoneNumber);

        if (existingOtp != null) {
            LocalDateTime now = LocalDateTime.now();
            if (existingOtp.getExpiresAt().minusMinutes(5).plusSeconds(60).isAfter(now)) {
                throw new RuntimeException("Please wait before requesting a new OTP.");
            }
        }
       generateOtp(phoneNumber);

    }

    @Override
    public void verifyOtp(String contact, String inputOtp) {
        VerificationOtp stored = otpStorage.get(contact);

        if (stored == null) throw new RuntimeException("No OTP found.");

        boolean isValid = stored.getCode().equals(inputOtp) &&
                stored.getExpiresAt().isAfter(LocalDateTime.now());

        if(!isValid){
            throw new RuntimeException("Invalid verification code");
        }

        otpStorage.remove(contact);
    }

//    @Override
//    public void verifyEmailOtp(String email, String inputCode) {
//        VerificationOtp storedOtp = otpStorage.get(email);
//
//        if (storedOtp == null) {
//        }
//
//        boolean isValid = storedOtp.getCode().equals(inputCode)
//                && storedOtp.getExpiresAt().isAfter(LocalDateTime.now());
//
//        if (!isValid) {
//            throw new RuntimeException("Invalid or expired OTP.");
//        }
//        otpStorage.remove(email);
//    }


    @Override
    public void storePendingUserRequest(CreateUserRequest request) {
        pendingUserRequests.put(request.getPhoneNumber(), request);
    }

    @Override
    public CreateUserRequest getPendingUserRequest(String phoneNumber) {
        return pendingUserRequests.get(phoneNumber);
    }

    private void generateOtp(String contact){
        String code = String.format("%06d", random.nextInt(1000000));
        smsService.sendSms(contact, "Your verification code is: " + code);

        VerificationOtp otpEntry = new VerificationOtp(code, LocalDateTime.now().plusMinutes(5));
        logger.info("Stored PhoneNumber is {}", contact);
        otpStorage.put(contact, otpEntry);
        logger.info("Otp stored to storage {}", otpStorage.get(contact));
    }

}
