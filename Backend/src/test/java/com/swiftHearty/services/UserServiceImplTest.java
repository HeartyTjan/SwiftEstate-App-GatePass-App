package com.swiftHearty.services;

import com.swiftHearty.data.model.User;
import com.swiftHearty.data.repository.SecurityProfileRepository;
import com.swiftHearty.data.repository.TenantProfileRepository;
import com.swiftHearty.data.repository.UserRepository;
import com.swiftHearty.dto.request.*;
import com.swiftHearty.dto.response.CreateNewUserResponse;
import com.swiftHearty.dto.response.UserLoginResponse;
import com.swiftHearty.exception.PhoneNumberNotWhitelistedException;
import com.swiftHearty.services.impl.UserServiceImpl;
import com.swiftHearty.utils.jwt.JwtUtil;
import com.swiftHearty.utils.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock private UserMapper userMapper;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AdminService adminService;
    @Mock private VerificationOtpService verificationOtpService;
    @Mock private TenantProfileRepository tenantProfileRepository;
    @Mock private SecurityProfileRepository securityProfileRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private Authentication authentication;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should throw exception if phone number is not whitelisted")
    void testCreateUser_throwsException_whenPhoneNumberNotWhitelisted() {
        CreateUserRequest request = new CreateUserRequest();
        request.setPhoneNumber("08012345678");

        when(adminService.existBy("08012345678")).thenReturn(false);

        assertThrows(PhoneNumberNotWhitelistedException.class, () -> userService.createUser(request));
    }

    @Test
    @DisplayName("Should throw exception if user already exists by phone number")
    void testCreateUser_throwsException_whenUserAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setPhoneNumber("08012345678");

        when(adminService.existBy("08012345678")).thenReturn(true);
        when(userRepository.existsByPhoneNumber("08012345678")).thenReturn(true);

        assertThrows(DuplicateKeyException.class, () -> userService.createUser(request));
    }

    @Test
    @DisplayName("Should call generateAndSendOtp if user request is valid")
    void testCreateUser_callsGenerateAndSendOtp_whenValidRequest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setPhoneNumber("08012345678");

        when(adminService.existBy("08012345678")).thenReturn(true);
        when(userRepository.existsByPhoneNumber("08012345678")).thenReturn(false);

        userService.createUser(request);

        verify(verificationOtpService, times(1)).generateAndSendOtp(request);
    }

    @Test
    @DisplayName("Should complete registration and create user successfully")
    void testCompleteRegistration_successfullyCreatesUser() {
        String phone = "08012345678";
        String otpCode = "123456";

        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setContact(phone);
        request.setCode(otpCode);

        CreateUserRequest pendingUserRequest = new CreateUserRequest();
        pendingUserRequest.setPhoneNumber(phone);
        pendingUserRequest.setRole("TENANT");

        User user = new User();
        user.setId("user123");
        user.setPhoneNumber(phone);
        user.setRole("TENANT");

        CreateNewUserResponse expectedResponse = new CreateNewUserResponse();
        expectedResponse.setId("user123");

        when(verificationOtpService.getPendingUserRequest(phone)).thenReturn(pendingUserRequest);
        when(userMapper.requestToUser(pendingUserRequest, passwordEncoder)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.userToResponse(any(User.class))).thenReturn(expectedResponse);

        CreateNewUserResponse response = userService.completeRegistration(request);

        assertNotNull(response);
        assertEquals("user123", response.getId());

        verify(tenantProfileRepository, times(1)).save(any());
        verify(securityProfileRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception if role is not SECURITY for security creation")
    void testCreateSecurity_failsIfRoleNotSecurity() {
        CreateSecurityRequest request = new CreateSecurityRequest();
        request.setRole("TENANT");
        request.setPhoneNumber("08012345678");

        assertThrows(IllegalArgumentException.class, () -> adminService.createSecurity(request));
    }

    @Test
    @DisplayName("Should login user successfully and return token in response")
    void testLogin_Success() {
        UserLoginRequest request = new UserLoginRequest();
        request.setPhoneNumber("2348012345678");
        request.setPassword("password123");

        User user = new User();
        user.setPhoneNumber("2348012345678");
        user.setRole("TENANT");

        String fakeToken = "fake-jwt-token";
        UserLoginResponse expectedResponse = new UserLoginResponse();
        expectedResponse.setMessage("Login Successfully");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(user.getPhoneNumber(), user.getRole())).thenReturn(fakeToken);
        when(userMapper.mapToLoginResponse(fakeToken, user, "Login Successfully")).thenReturn(expectedResponse);

        UserLoginResponse actualResponse = userService.login(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil, times(1)).generateToken(user.getPhoneNumber(), user.getRole());
        verify(userMapper, times(1)).mapToLoginResponse(fakeToken, user, "Login Successfully");

        assertEquals(authentication, SecurityContextHolder.getContext().getAuthentication());
    }
}
