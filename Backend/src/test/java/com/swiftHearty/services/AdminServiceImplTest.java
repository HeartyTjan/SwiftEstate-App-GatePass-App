package com.swiftHearty.services;

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
import com.swiftHearty.services.impl.AdminServiceImpl;
import com.swiftHearty.utils.mappers.UserMapper;
import com.swiftHearty.utils.mappers.WhiteListMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @InjectMocks
    private AdminServiceImpl adminService;

    @Mock
    private WhiteListRepository whiteListRepository;

    @Mock
    private WhiteListMapper whiteListMapper;

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;



    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    SecurityProfileRepository securityProfileRepository;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should throw exception if phone number already exists in whitelist")
    void testAddUserToWhiteList_ThrowsException_WhenPhoneNumberExists() {
        AddWhiteListRequest request = new AddWhiteListRequest();
        request.setPhoneNumber("08012345678");

        when(whiteListRepository.existsByPhoneNumber("08012345678")).thenReturn(true);

        assertThrows(ResourceAllReadyExistException.class, () -> {
            adminService.addUserToWhiteList(request);
        });

        verify(whiteListRepository, times(1)).existsByPhoneNumber("08012345678");
        verify(whiteListRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save phone number and return response when not already whitelisted")
    void testAddUserToWhiteList_SuccessfullyAddsPhoneNumber() {
        AddWhiteListRequest request = new AddWhiteListRequest();
        request.setPhoneNumber("08012345678");

        WhiteList whiteListEntity = new WhiteList();
        whiteListEntity.setPhoneNumber("08012345678");

        WhiteListResponse response = new WhiteListResponse();
        response.setPhoneNumber("08012345678");

        when(whiteListRepository.existsByPhoneNumber("08012345678")).thenReturn(false);
        when(whiteListMapper.requestToWhiteList(request)).thenReturn(whiteListEntity);
        when(whiteListMapper.whiteListToResponse(whiteListEntity)).thenReturn(response);

        WhiteListResponse result =adminService.addUserToWhiteList(request);

        assertNotNull(result);
        assertEquals("08012345678", result.getPhoneNumber());

        verify(whiteListRepository, times(1)).existsByPhoneNumber("08012345678");
        verify(whiteListRepository, times(1)).save(whiteListEntity);
        verify(whiteListMapper, times(1)).whiteListToResponse(whiteListEntity);
    }

    @Test
    @DisplayName("Should return true if phone number exists in whitelist")
    void testExistBy_ReturnsTrue_WhenPhoneNumberExists() {
        when(whiteListRepository.existsByPhoneNumber("08012345678")).thenReturn(true);

        boolean exists = adminService.existBy("08012345678");

        assertTrue(exists);
        verify(whiteListRepository, times(1)).existsByPhoneNumber("08012345678");
    }

    @Test
    @DisplayName("Should return false if phone number does not exist in whitelist")
    void testExistBy_ReturnsFalse_WhenPhoneNumberDoesNotExist() {
        when(whiteListRepository.existsByPhoneNumber("08000000000")).thenReturn(false);

        boolean exists = adminService.existBy("08000000000");

        assertFalse(exists);
        verify(whiteListRepository, times(1)).existsByPhoneNumber("08000000000");
    }

    @Test
    @DisplayName("Should create a SECURITY user and save security profile")
    void testCreateSecurity_successfullyCreatesSecurityUser() {
        CreateSecurityRequest request = new CreateSecurityRequest();
        request.setPhoneNumber("08012345678");
        request.setRole("SECURITY");

        User user = new User();
        user.setId("sec123");
        user.setRole("SECURITY");

        String randomPassword = "123456";

        CreateNewUserResponse expectedResponse = new CreateNewUserResponse();
        expectedResponse.setId("sec123");

        when(userRepository.existsByPhoneNumber("08012345678")).thenReturn(false);
        when(userMapper.requestToUser(request, passwordEncoder, randomPassword)).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(expectedResponse);

        CreateNewUserResponse response = adminService.createSecurity(request);

        assertNotNull(response);
        assertEquals("sec123", response.getId());
        verify(securityProfileRepository, times(1)).save(any());
    }
}
