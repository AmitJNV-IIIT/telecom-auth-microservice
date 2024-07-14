package com.excitel.controller;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.excitel.dto.NewPasswordDTO;
import com.excitel.dto.ResponseDTO;
import com.excitel.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.management.ServiceNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PasswordControllerTest {

    @Mock
    private PasswordService passwordService;

    @InjectMocks
    private PasswordController passwordController;

    private final String mockMobileNumber = "1234567890";
    private final String mockOTP = "123456";
    private final String mockNewPassword = "newPassword";
    private final String mockKeyAndHash = "key:hash";

    private NewPasswordDTO mockNewPasswordDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockNewPasswordDto = new NewPasswordDTO();
        mockNewPasswordDto.setStatus("update Password");
        mockNewPasswordDto.setMobileNumber(mockMobileNumber);
        mockNewPasswordDto.setNewPassword(mockNewPassword);
        mockNewPasswordDto.setKeyAndHash(mockKeyAndHash);
    }

    @Test
    void forgotPassword_ValidMobileNumber_Success() {
        // Arrange
        when(passwordService.forgotPassword(mockMobileNumber)).thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<?> response = passwordController.forgotPassword(mockMobileNumber);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void verifyOTP_ValidRequest_Success() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("mobileNumber", mockMobileNumber);
        request.put("otp", mockOTP);
        when(passwordService.verifyOTP(mockMobileNumber, mockOTP)).thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<?> response = passwordController.verifyOTP(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testSendOTP_Success() {
        // Mock the service method
        when(passwordService.sendOTP(anyString())).thenReturn(ResponseEntity.ok(new ResponseDTO(HttpStatus.OK, "Success", "OTP sent successfully")));

        // Call the controller method
        ResponseEntity<ResponseDTO> response = passwordController.sendOTP("1234567890");

        // Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody().getMessage());
        assertEquals("OTP sent successfully", response.getBody().getResponse());

        // Verify that the service method was called with the correct parameter
        verify(passwordService, times(1)).sendOTP("1234567890");
    }

    @Test
    void testVerifyOTP_Success() {
        // Mock the request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("mobileNumber", "1234567890");
        requestBody.put("otp", "1234");

        // Mock the service method
        when(passwordService.verifyOTP(anyString(), anyString())).thenReturn(ResponseEntity.ok(new ResponseDTO(HttpStatus.OK, "Success", "OTP verified successfully")));

        // Call the controller method
        ResponseEntity<ResponseDTO> response = passwordController.verifyOTP(requestBody);

        // Verify the response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Success", response.getBody().getMessage());
        assertEquals("OTP verified successfully", response.getBody().getResponse());

        // Verify that the service method was called with the correct parameters
        verify(passwordService, times(1)).verifyOTP("1234567890", "1234");
    }

    @Test
    void testResetPassword_SuccessfulUpdate() throws Exception {
        // Mock request
        NewPasswordDTO request = new NewPasswordDTO();
        request.setStatus("update Password");
        request.setMobileNumber("1234567890");
        request.setNewPassword("newPassword");
        request.setKeyAndHash("keyAndHash");

        // Mock service response
        when(passwordService.updatePassword(any(), any(), any())).thenReturn(true);

        // Call the controller method
        ResponseEntity<ResponseDTO> response = passwordController.resetPassword(request);

        // Verify response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password Updated", response.getBody().getMessage());
        assertEquals("Login again", response.getBody().getResponse());
    }

    @Test
    void testResetPassword_UnauthorizedUser() {
        // Mock request
        NewPasswordDTO request = new NewPasswordDTO();
        request.setStatus("invalid status");
        request.setMobileNumber("1234567890");
        request.setNewPassword("newPassword");
        request.setKeyAndHash("keyAndHash");

        // Call the controller method
        ResponseEntity<ResponseDTO> response = passwordController.resetPassword(request);

        // Verify response
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized User", response.getBody().getMessage());
        assertEquals("Authentication is required to access this resource", response.getBody().getResponse());
    }

    @Test
    void testResetPassword_ServerDownException() throws Exception {
        // Mock request
        NewPasswordDTO request = new NewPasswordDTO();
        request.setStatus("update Password");
        request.setMobileNumber("1234567890");
        request.setNewPassword("newPassword");
        request.setKeyAndHash("keyAndHash");

        // Mock service to throw exception
        when(passwordService.updatePassword(any(), any(), any())).thenThrow(new RuntimeException());

        // Call the controller method
        ResponseEntity<ResponseDTO> response = passwordController.resetPassword(request);

        // Verify response
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Server Down", response.getBody().getMessage());
        assertEquals("LETS TRY AFTER SOMETIME", response.getBody().getResponse());
    }

    @Test
    public void testResetPassword_Success() throws Exception {
        NewPasswordDTO request = new NewPasswordDTO("update Password", "1234567890", "newPassword", "keyAndHash");
        when(passwordService.updatePassword(request.getNewPassword(), request.getMobileNumber(), request.getKeyAndHash())).thenReturn(true);

        ResponseEntity<ResponseDTO> response = passwordController.resetPassword(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password Updated", response.getBody().getMessage());
    }

    @Test
    public void testResetPassword_UpdatePasswordFails() throws Exception {
        NewPasswordDTO request = new NewPasswordDTO("update Password", "1234567890", "newPassword", "keyAndHash");
        when(passwordService.updatePassword(request.getNewPassword(), request.getMobileNumber(), request.getKeyAndHash())).thenReturn(false);

        ResponseEntity<ResponseDTO> response = passwordController.resetPassword(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized User", response.getBody().getMessage());
    }

    @Test
    public void testResetPassword_InvalidStatus() throws Exception {
        NewPasswordDTO request = new NewPasswordDTO("invalidStatus", "1234567890", "newPassword", "keyAndHash");

        ResponseEntity<ResponseDTO> response = passwordController.resetPassword(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Unauthorized User", response.getBody().getMessage());
    }
}
