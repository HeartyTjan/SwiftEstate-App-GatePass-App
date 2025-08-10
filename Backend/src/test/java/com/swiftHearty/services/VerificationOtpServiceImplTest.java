package com.swiftHearty.services;

import com.swiftHearty.data.model.VerificationOtp;
import com.swiftHearty.dto.request.CreateUserRequest;
import com.swiftHearty.services.impl.VerificationOtpServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationOtpServiceImplTest {

    @Mock
    private SmsService smsService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private VerificationOtpServiceImpl verificationOtpService;

    private String phoneNumber;
    private String email;
    private String name;
    private CreateUserRequest userRequest;
    private VerificationOtp otpEntry;

    @BeforeEach
    void setUp() {
        phoneNumber = "1234567890";
        email = "test@example.com";
        name = "John Doe";

        userRequest = new CreateUserRequest();
        userRequest.setPhoneNumber(phoneNumber);

        otpEntry = new VerificationOtp("123456", LocalDateTime.now().plusMinutes(5));
    }

    @Test
    @DisplayName("Should generate and send OTP via SMS successfully")
    void testGenerateAndSendOtp() {
        verificationOtpService.generateAndSendOtp(phoneNumber);

        assertTrue(verificationOtpService.getOtpStorage().containsKey(phoneNumber));

        VerificationOtp storedOtp = verificationOtpService.getOtpStorage().get(phoneNumber);
        assertNotNull(storedOtp);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(smsService).sendSms(eq(phoneNumber), messageCaptor.capture());

        String expectedMessage = "Your verification code is: " + storedOtp.getCode();
        assertEquals(expectedMessage, messageCaptor.getValue());
    }

    @Test
    @DisplayName("Should not store OTP if SMS fails")
    void testGenerateAndSendOtp_WithSmsException() {
        doThrow(new RuntimeException("SMS failed"))
                .when(smsService)
                .sendSms(eq(phoneNumber), anyString());

        assertThrows(RuntimeException.class, () -> verificationOtpService.generateAndSendOtp(phoneNumber));
        assertFalse(verificationOtpService.getOtpStorage().containsKey(phoneNumber));
    }


    @Test
    @DisplayName("Should generate and send email OTP successfully")
    void testGenerateAndSendEmailOtp() {
        verificationOtpService.generateAndSendEmailOtp(email, name);

        assertTrue(verificationOtpService.getOtpStorage().containsKey(email));
        VerificationOtp storedOtp = verificationOtpService.getOtpStorage().get(email);
        assertNotNull(storedOtp);
        assertNotNull(storedOtp.getCode());

        verify(emailService, times(1)).sendEmail(eq(email), anyString(), contains(storedOtp.getCode()));
    }

    @Test
    @DisplayName("Should verify valid OTP")
    void testVerifyOtp_ValidCode() {
        verificationOtpService.getOtpStorage().put(phoneNumber, otpEntry);

        assertDoesNotThrow(() -> verificationOtpService.verifyOtp(phoneNumber, "123456"));

        assertFalse(verificationOtpService.getOtpStorage().containsKey(phoneNumber));
    }

    @Test
    @DisplayName("Should throw on invalid OTP")
    void testVerifyOtp_InvalidCode() {
        verificationOtpService.getOtpStorage().put(phoneNumber, otpEntry);

        Exception ex = assertThrows(RuntimeException.class,
                () -> verificationOtpService.verifyOtp(phoneNumber, "654321"));

        assertEquals("Invalid verification code", ex.getMessage());
        assertTrue(verificationOtpService.getOtpStorage().containsKey(phoneNumber));
    }

    @Test
    @DisplayName("Should throw on expired OTP")
    void testVerifyOtp_ExpiredCode() {
        VerificationOtp expiredOtp = new VerificationOtp("123456", LocalDateTime.now().minusMinutes(1));
        verificationOtpService.getOtpStorage().put(phoneNumber, expiredOtp);

        Exception ex = assertThrows(RuntimeException.class,
                () -> verificationOtpService.verifyOtp(phoneNumber, "123456"));

        assertEquals("Invalid verification code", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw if OTP not found")
    void testVerifyOtp_NullStorage() {
        Exception ex = assertThrows(RuntimeException.class,
                () -> verificationOtpService.verifyOtp(phoneNumber, "123456"));

        assertEquals("No OTP found.", ex.getMessage());
    }

    @Test
    @DisplayName("Should store pending user request")
    void testStorePendingUserRequest() {
        verificationOtpService.storePendingUserRequest(userRequest);

        assertTrue(verificationOtpService.getPendingUserRequests().containsKey(phoneNumber));
        assertEquals(userRequest, verificationOtpService.getPendingUserRequest(phoneNumber));
    }

    @Test
    @DisplayName("Should return existing pending user request")
    void testGetPendingUserRequest_Existing() {
        verificationOtpService.getPendingUserRequests().put(phoneNumber, userRequest);

        CreateUserRequest retrievedRequest = verificationOtpService.getPendingUserRequest(phoneNumber);

        assertNotNull(retrievedRequest);
        assertEquals(userRequest, retrievedRequest);
    }

    @Test
    @DisplayName("Should return null for non-existing pending user")
    void testGetPendingUserRequest_NonExisting() {
        CreateUserRequest retrievedRequest = verificationOtpService.getPendingUserRequest(phoneNumber);

        assertNull(retrievedRequest);
    }

    @Test
    @DisplayName("Should enforce cooldown period on OTP resend")
    void testGenerateAndSendOtp_CooldownEnforced() {
        VerificationOtp freshOtp = new VerificationOtp("111111", LocalDateTime.now().plusMinutes(5));
        verificationOtpService.getOtpStorage().put(phoneNumber, freshOtp);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                verificationOtpService.generateAndSendOtp(phoneNumber));

        assertEquals("Please wait before requesting a new OTP.", ex.getMessage());
    }
}
