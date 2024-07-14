package com.excitel.serviceimpl;

import com.excitel.exception.custom.DynamoDBConnectionException;
import com.excitel.model.OtpDB;
import com.excitel.redishelper.OTPRedisHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.management.ServiceNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OTPServiceImplTest {

    @Mock
    private OTPRedisHelper otpRedisHelper;

    @InjectMocks
    private OTPServiceImpl otpService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGenerateOTP() {
        // When
        String generatedOTP = otpService.generateOTP();

        // Then
        assertNotNull(generatedOTP);
        assertEquals(6, generatedOTP.length());
    }

//    @Test
//    public void testStoreOTP_Success() throws Exception {
//        // Given
//        String mobileNumber = "1234567890";
//        String otp = "123456";
//        OtpDB otpDB = new OtpDB(mobileNumber, otp, "", "");
//
//        // Mock behavior
//        doNothing().when(otpRedisHelper).saveOTP(mobileNumber, otpDB);
//
//        // When
//        otpService.storeOTP(mobileNumber, otp);
//
//        // Then
//        // Create an OtpDB instance to match the expected otpDB
//        OtpDB expectedOtpDB = new OtpDB(mobileNumber, otp, "", ""); // Match the fields used for verification
//
//        // Verify that saveOTP method was called with the correct arguments
//        verify(otpRedisHelper, times(1)).saveOTP(mobileNumber, expectedOtpDB);
//    }

    @Test
    public void testStoreOTP_Exception() throws Exception {
        // Given
        String mobileNumber = "1234567890";
        String otp = "123456";

        // Mock behavior to throw an exception
        doThrow(new DynamoDBConnectionException("DynamoDB connection error")).when(otpRedisHelper).saveOTP(anyString(), any(OtpDB.class));

        // When & Then
        assertThrows(DynamoDBConnectionException.class, () -> {
            otpService.storeOTP(mobileNumber, otp);
        });
    }

    @Test
    public void testVerifyOTP_ValidOTP() throws Exception {
        // Given
        String mobileNumber = "1234567890";
        String otp = "123456";
        OtpDB otpDB = new OtpDB(mobileNumber, otp, "", "");

        // Mock behavior
        when(otpRedisHelper.getOtp(mobileNumber)).thenReturn(otpDB);

        // When
        String verificationResult = otpService.verifyOTP(mobileNumber, otp);

        // Then
        assertEquals("verified", verificationResult);
    }

    @Test
    public void testVerifyOTP_InvalidOTP() throws Exception {
        // Given
        String mobileNumber = "1234567890";
        String otp = "123456";
        String storedOTP = "654321"; // Incorrect OTP

        // Mock behavior
        OtpDB otpDB = new OtpDB(mobileNumber, storedOTP, "", "");
        when(otpRedisHelper.getOtp(mobileNumber)).thenReturn(otpDB);

        // When
        String verificationResult = otpService.verifyOTP(mobileNumber, otp);

        // Then
        assertEquals("incorrect otp", verificationResult);
    }

    @Test
    public void testVerifyOTP_ExpiredOTP() throws Exception {
        // Given
        String mobileNumber = "1234567890";
        String otp = "123456";

        // Mock behavior to return null (indicating expired OTP)
        when(otpRedisHelper.getOtp(mobileNumber)).thenReturn(null);

        // When
        String verificationResult = otpService.verifyOTP(mobileNumber, otp);

        // Then
        assertEquals("otp expired", verificationResult);
    }
    @Test
    public void testVerifyOTPThrowsServiceNotFoundException() throws IOException {
        String mobileNumber = "1234567890";
        String otp = "123456";
        when(otpRedisHelper.getOtp(mobileNumber)).thenThrow(new IOException("Test exception"));

        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            otpService.verifyOTP(mobileNumber, otp);
        });

        assertTrue(exception.getMessage().contains("Test exception"));
    }
    @Test
    public void testStoreOTPWithClientID() throws Exception {
        // Mock data
        String mobileNumber = "1234567890";
        String clientID = "clientId";
        String hashValue = "hashValue";

        OtpDB otpDBMock = Mockito.mock(OtpDB.class);

        // Mock behavior of OTPRedisHelper
        when(otpRedisHelper.getOtp(any())).thenReturn(otpDBMock); // Mock getOtp to return the mocked OtpDB object

        // Test storeOTPWithClientID method
        String result = otpService.storeOTPWithClientID(mobileNumber, clientID, hashValue);

        // Verify interactions and assertions
        assertEquals("Hash_pair = clientId: hashValue", result); // Check the expected return value

        // Verify that setClientID and setHashKey were called on the mocked OtpDB object
        verify(otpDBMock).setClientID(clientID);
        verify(otpDBMock).setHashKey(hashValue);

        // Verify that saveOTP method was called with the correct parameters
        verify(otpRedisHelper).saveOTP(mobileNumber, otpDBMock);
    }
    @Test
    public void testStoreOTP_ExceptionThrown() throws Exception {
        // Prepare test data
        String mobileNumber = "1234567890";
        String otp = "123456";

        // Mock behavior of otpRedisHelper.saveOTP() to throw an exception
        doThrow(new DynamoDBConnectionException("DynamoDB connection failed")).when(otpRedisHelper).saveOTP(anyString(), any(OtpDB.class));

        // Call the method under test and assert that it throws DynamoDBConnectionException
        Exception exception = assertThrows(DynamoDBConnectionException.class, () -> {
            otpService.storeOTP(mobileNumber, otp);
        });

        // Verify that the thrown exception contains the expected message
        assertTrue(exception.getMessage().contains("DynamoDB connection failed"));

        // Verify that saveOTP method was called with correct arguments
        verify(otpRedisHelper, times(1)).saveOTP(eq(mobileNumber), any(OtpDB.class));
    }
    @Test
    public void testStoreOtpThrowsServiceNotFoundException() throws IOException {
        String mobileNumber = "1234567890";
        String otp = "123456";
        doThrow(new IOException("Test exception")).when(otpRedisHelper).saveOTP(eq(mobileNumber), any(OtpDB.class));

        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            otpService.storeOTP(mobileNumber, otp);
        });

        assertEquals("Test exception", exception.getMessage());
    }
    @Test
    public void testStoreOTP_Successful() throws Exception {
        // Prepare test data
        String mobileNumber = "1234567890";
        String otp = "123456";

        // Mock behavior of otpRedisHelper.saveOTP() to succeed
        doNothing().when(otpRedisHelper).saveOTP(anyString(), any(OtpDB.class));

        // Call the method under test
        assertDoesNotThrow(() -> {
            otpService.storeOTP(mobileNumber, otp);
        });

        // Verify that saveOTP method was called with correct arguments
        verify(otpRedisHelper, times(1)).saveOTP(eq(mobileNumber), any(OtpDB.class));
    }
    @Test
    public void testStoreOTPWithClientIDThrowsServiceNotFoundException() throws IOException {
        String mobileNumber = "1234567890";
        String clientID = "testClient";
        String hashValue = "testHash";
        when(otpRedisHelper.getOtp(mobileNumber)).thenThrow(new IOException("Test exception"));

        Exception exception = assertThrows(ServiceNotFoundException.class, () -> {
            otpService.storeOTPWithClientID(mobileNumber, clientID, hashValue);
        });

        assertTrue(exception.getMessage().contains("Test exception"));
    }
}
