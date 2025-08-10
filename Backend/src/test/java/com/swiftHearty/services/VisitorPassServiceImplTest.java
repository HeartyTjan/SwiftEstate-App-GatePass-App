package com.swiftHearty.services;

import com.swiftHearty.data.model.AccessCode;
import com.swiftHearty.data.model.VisitorPass;
import com.swiftHearty.data.repository.VisitorPassRepository;
import com.swiftHearty.dto.response.VisitorPassResponse;
import com.swiftHearty.exception.ResourceNotFoundException;
import com.swiftHearty.services.impl.VisitorPassServiceImpl;
import com.swiftHearty.utils.mappers.VisitorPassMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorPassServiceImplTest {

    @Mock
    private VisitorPassRepository visitorPassRepository;

    @Mock
    private VisitorPassMapper visitorPassMapper;

    @InjectMocks
    private VisitorPassServiceImpl visitorPassService;

    private AccessCode accessCode;
    private VisitorPass visitorPass;
    private VisitorPassResponse visitorPassResponse;
    private String passId;
    private String securityName;

    @BeforeEach
    void setUp() {
        passId = "VP123";
        securityName = "SecurityGuard";
        accessCode = new AccessCode();
        accessCode.setCode("123456");
        visitorPass = new VisitorPass();
        visitorPass.setId(passId);
        visitorPass.setOpen(true);
        visitorPass.setTimeOut(null);
        visitorPassResponse = new VisitorPassResponse();
        visitorPassResponse.setVisitorPass(visitorPass);
    }

    @Test
    @DisplayName("Should generate and return a new VisitorPass")
    void testGenerateVisitorPass() {
        when(visitorPassMapper.mapToPass(accessCode, securityName)).thenReturn(visitorPass);
        when(visitorPassRepository.save(any(VisitorPass.class))).thenReturn(visitorPass);

        VisitorPass result = visitorPassService.generateVisitorPass(accessCode, securityName);

        assertNotNull(result);
        assertEquals(passId, result.getId());
        verify(visitorPassRepository, times(1)).save(any(VisitorPass.class));
    }

    @Test
    @DisplayName("Should retrieve VisitorPass with valid ID")
    void testRetrieveVisitorPass_ValidId() {
        when(visitorPassRepository.findVisitorPassById(passId)).thenReturn(Optional.of(visitorPass));
        when(visitorPassMapper.mapToResponse(visitorPass)).thenReturn(visitorPassResponse);

        VisitorPassResponse response = visitorPassService.retrieveVisitorPass(passId);

        assertNotNull(response);
        assertEquals(passId, response.getVisitorPass().getId());
        verify(visitorPassRepository, times(1)).findVisitorPassById(passId);
        verify(visitorPassMapper, times(1)).mapToResponse(visitorPass);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for invalid VisitorPass ID")
    void testRetrieveVisitorPass_InvalidId() {
        when(visitorPassRepository.findVisitorPassById(passId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                visitorPassService.retrieveVisitorPass(passId));
        verify(visitorPassRepository, times(1)).findVisitorPassById(passId);
        verify(visitorPassMapper, never()).mapToResponse(any(VisitorPass.class));
    }

    @Test
    @DisplayName("Should close VisitorPass and return updated response for valid ID")
    void testUpdatePass_ValidId() {
        when(visitorPassRepository.findVisitorPassById(passId)).thenReturn(Optional.of(visitorPass));
        when(visitorPassMapper.mapToResponse(visitorPass)).thenReturn(visitorPassResponse);

        VisitorPassResponse response = visitorPassService.closeVisitorPass(passId);

        assertNotNull(response);
        assertEquals(passId, response.getVisitorPass().getId());
        assertFalse(visitorPass.isOpen());
        assertNotNull(visitorPass.getTimeOut());
        verify(visitorPassRepository, times(1)).findVisitorPassById(passId);
        verify(visitorPassRepository, times(1)).save(visitorPass);
        verify(visitorPassMapper, times(1)).mapToResponse(visitorPass);
    }

    @Test
    @DisplayName("Should throw exception if VisitorPass is already closed")
    void testUpdatePass_AlreadyExited() {
        visitorPass.setTimeOut(LocalDateTime.now());
        when(visitorPassRepository.findVisitorPassById(passId)).thenReturn(Optional.of(visitorPass));

        assertThrows(IllegalArgumentException.class, () ->
                visitorPassService.closeVisitorPass(passId));
        verify(visitorPassRepository, times(1)).findVisitorPassById(passId);
        verify(visitorPassRepository, never()).save(any(VisitorPass.class));
        verify(visitorPassMapper, never()).mapToResponse(any(VisitorPass.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when closing VisitorPass with invalid ID")
    void testUpdatePass_InvalidId() {
        when(visitorPassRepository.findVisitorPassById(passId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                visitorPassService.closeVisitorPass(passId));
        verify(visitorPassRepository, times(1)).findVisitorPassById(passId);
        verify(visitorPassRepository, never()).save(any(VisitorPass.class));
        verify(visitorPassMapper, never()).mapToResponse(any(VisitorPass.class));
    }
}
