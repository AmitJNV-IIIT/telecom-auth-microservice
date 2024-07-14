package com.excitel.serviceimpl;
import com.excitel.dto.ResponseDTO;
import com.excitel.encryption.PasswordDecoder;
import com.excitel.exception.custom.UserNotFoundException;
import com.excitel.model.Customer;
import com.excitel.model.OtpDB;
import com.excitel.redishelper.AuthRedis;
import com.excitel.redishelper.OTPRedisHelper;
import com.excitel.repository.AuthDynamoRepository;
import com.excitel.security.UserDetailsServiceImpl;
import com.excitel.service.OTPService;
import com.excitel.service.UserService;
import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import javax.management.ServiceNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static com.excitel.constant.AppConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PasswordServiceImplTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    PasswordDecoder passwordDecoder;

    @Mock
    private AuthDynamoRepository authDynamoRepository;

    @Mock
    private EmailService emailService;


    @Mock
    private OTPService otpService;

    @Mock
    OTPRedisHelper otpRedisHelper;


    @Mock
    AuthRedis authRedis;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private OtpDB otpDB;

    private Logger logger;
    @BeforeEach void setUp() {
        MockitoAnnotations.openMocks(this);
//        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testGenerateClientID() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        Method generateClientIDMethod = PasswordServiceImpl.class.getDeclaredMethod("generateClientID");
        generateClientIDMethod.setAccessible(true);

        // Act
        String clientID = (String) generateClientIDMethod.invoke(passwordService);

        // Assert
        assertEquals(7, clientID.length()); // Assuming CLIENT_ID_LENGTH is 7
    }

    @Test
    void testGenerateHashKey() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        Method generateHashKeyMethod = PasswordServiceImpl.class.getDeclaredMethod("generateHashKey");
        generateHashKeyMethod.setAccessible(true);

        // Act
        String hashKey = (String) generateHashKeyMethod.invoke(passwordService);

        // Assert
        assertEquals(11, hashKey.length()); // Assuming HASH_KEY_LENGTH is 11
    }

    @Test
    void testGenerateRandomString() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Arrange
        Method generateRandomStringMethod = PasswordServiceImpl.class.getDeclaredMethod("generateRandomString", String.class, int.class);
        generateRandomStringMethod.setAccessible(true);

        // Act
        String randomString = (String) generateRandomStringMethod.invoke(passwordService, "abc", 5);

        // Assert
        assertEquals(5, randomString.length());
    }

    @Test
    void testGenerateRandomString_InvalidChars() throws NoSuchMethodException {
        // Arrange
        Method generateRandomStringMethod = PasswordServiceImpl.class.getDeclaredMethod("generateRandomString", String.class, int.class);
        generateRandomStringMethod.setAccessible(true);

        // Act & Assert
        try {
            generateRandomStringMethod.invoke(passwordService, "", 5);
            fail("Expected IllegalArgumentException was not thrown");
        } catch (InvocationTargetException | IllegalAccessException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof IllegalArgumentException);
        }
    }

    @Test
    void forgotPassword_UnregisteredUser_ReturnsUnauthorized() {
        // Arrange
        String mobileNumber = "1234567890";
        when(userService.loadUserByUsername(mobileNumber)).thenReturn(null);

        // Act
        ResponseEntity<?> response = passwordService.forgotPassword(mobileNumber);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        // Add assertions for the response body if needed
    }

    @Test
    void testVerifyOTP_WrongOTP_BadCredentials() throws Exception {
        // Arrange
        String mobileNumber = "1234567890";
        String otp = "123456";
        when(userService.loadUserByUsername(mobileNumber)).thenReturn(new Customer());
        when(otpService.verifyOTP(mobileNumber, otp)).thenReturn("some other value");

        // Act
        ResponseEntity<?> responseEntity = passwordService.verifyOTP(mobileNumber, otp);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        // Add assertions for the response body if needed
    }
    @Test
    void testVerifyOTP_ExceptionCaught() throws Exception{
        String mobileNumber = "1234567890";
        String otp = "123456";
        when(otpService.verifyOTP(eq(mobileNumber), eq(otp))).thenThrow(new RuntimeException("Service unavailable"));

        ResponseEntity<ResponseDTO> response = passwordService.verifyOTP(mobileNumber, otp);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Service Down", response.getBody().getMessage());
        assertEquals("TRY AFTER SOMETIME", response.getBody().getResponse());
        verify(otpService).verifyOTP(eq(mobileNumber), eq(otp));
    }

    @Test
    void forgotPassword_InvalidMobileNumber_ShouldReturnUnauthorized() {
        // Arrange
        String mobileNumber = "1234567890";

        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = passwordService.forgotPassword(mobileNumber);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("You are not a registered user", ((ResponseDTO) responseEntity.getBody()).getResponse());
        verify(emailService, Mockito.never()).sendConfirmationEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testForgotPassword_UserNotFound() {
        // Arrange
        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenReturn(null);

        // Act
        ResponseEntity<?> responseEntity = passwordService.forgotPassword("1234567890");

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, responseDTO.getStatus());
        assertEquals("Invalid Mobile Number", responseDTO.getMessage());
        assertEquals("You are not a registered user", responseDTO.getResponse());
    }

    @Test
    public void testVerifyOTP_CorrectOTP_Verified() throws Exception {
        // Arrange
        LocalDateTime currentTime = LocalDateTime.now();
        String otp = "123456";
        String mobileNumber = "1234567890";
        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenReturn(new Customer());
        when(otpService.verifyOTP(anyString(), anyString())).thenReturn("verified");

        // Act
        ResponseEntity<?> responseEntity = passwordService.verifyOTP(mobileNumber, otp);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.OK, responseDTO.getStatus());

        assertEquals("OTP Verified", responseDTO.getResponse());
    }

    @Test
    public void testVerifyOTP_ExpiredOTP() throws Exception {
        // Arrange
        LocalDateTime currentTime = LocalDateTime.now();
        String otp = "123456";
        String mobileNumber = "1234567890";
        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenReturn(new Customer());
        when(otpService.verifyOTP(anyString(), anyString())).thenReturn("otp expired");

        // Act
        ResponseEntity<?> responseEntity = passwordService.verifyOTP(mobileNumber, otp);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, responseDTO.getStatus());
        assertEquals("OTP Expired", responseDTO.getMessage());
        assertEquals("Resend OTP", responseDTO.getResponse());
    }

    @Test
    public void testVerifyOTP_IncorrectOTP_Provided() throws Exception {
        // Arrange
        String otp = "123456";
        String mobileNumber = "1234567890";
        when(authDynamoRepository.getUserByMobileNumber(anyString(), anyList())).thenReturn(new Customer());
        when(otpService.verifyOTP(anyString(), anyString())).thenReturn("incorrect otp");

        // Act
        ResponseEntity<?> responseEntity = passwordService.verifyOTP(mobileNumber, otp);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        ResponseDTO responseDTO = (ResponseDTO) responseEntity.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, responseDTO.getStatus());
        assertEquals("BAD CREDENTIALS", responseDTO.getMessage());
        assertEquals("Wrong OTP Provided", responseDTO.getResponse());
    }

    @Test
    void testSendOTP_Failure() throws Exception {
        // Arrange
        String mobileNumber = "1234567890";
        String email = "test@example.com";

        when(otpService.generateOTP()).thenThrow(new RuntimeException("Error generating OTP"));

        // Act
        ResponseEntity<ResponseDTO> response = passwordService.sendOTP(mobileNumber);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to send OTP", response.getBody().getResponse());

        // Verify interactions
        verify(otpService).generateOTP();
        verifyNoInteractions(emailService);
    }

    @Test
    void sendOtp_UserNotFound() {
        // Mock data
        String mobileNumber = "1234567890";

        // Mock OTP service to throw UserNotFoundException
        doThrow(UserNotFoundException.class).when(otpService).generateOTP();

        // Call the method
        ResponseEntity<ResponseDTO> responseEntity = passwordService.sendOTP(mobileNumber);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Failed to send OTP", responseEntity.getBody().getResponse());
    }
//    @Test
//    public void testSendOtp() throws ServiceNotFoundException {
//        String mobileNumber = "1234567890";
//        String otp = "1234";
//        String twilioUrl = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID.getValue() + "/Messages.json";
//        HttpHeaders headers = new HttpHeaders();
//
//        String requestBody = "From=" + FROM_NUMBER.getValue() +
//                "&To=91" + mobileNumber +
//                "&Body=Your Excitel OTP is " + otp;
//        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
//
//        ResponseEntity<String> responseEntity = new ResponseEntity<>("Success", HttpStatus.OK);
//        when(restTemplate.postForEntity(twilioUrl, request, String.class)).thenReturn(responseEntity);
//
//        ResponseDTO actualResponse = otpService.storeOTP(mobileNumber, otp); // use the actual method name
//
////        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
//        assertEquals("Success", actualResponse.getMessage());
////        assertEquals("OTP Send to the registered number", actualResponse.getDescription());
//
//        verify(restTemplate, times(1)).postForEntity(twilioUrl, request, String.class);
//    }
//    @Test
//    public void testStoreOTP() throws ServiceNotFoundException {
//        String mobileNumber = "1234567890";
//        String otp = "1234";
//
//        // Now call your storeOTP method.
//        otpService.storeOTP(mobileNumber, otp);
//
//        // After storing, probably you want to verify if it was stored correctly.
//        // Assuming you have a method getOTP which retrieves the OTP for a given mobile number.
//        String storedOtp = otpService.getOTP(mobileNumber);
//
//        // Now use an assertion to check if the OTP was stored correctly.
//        assertEquals(otp, storedOtp, "The stored OTP does not match the original OTP");
//    }

    @Test
    void forgotPassword_UserNotFoundException_ReturnsInternalServerError() {
        // Arrange
        String mobileNumber = "1234567890";
        when(authDynamoRepository.getUserByMobileNumber(eq(mobileNumber), anyList()))
                .thenThrow(new UserNotFoundException("User not found"));

        // Act
        ResponseEntity<?> response = passwordService.forgotPassword(mobileNumber);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        // Add assertions for the response body if needed
    }
    @Test
    void testUpdatePasswordWrongHash() throws Exception {
        String newPassword = "newPassword";
        String mobileNumber = "1234567890";
        String keyAndHash = "key=wrongClientID:hash=wrongHashValue";
        OtpDB otpDB = new OtpDB();
        otpDB.setClientID("clientID");
        otpDB.setHashKey("hashValue");
        Customer customer = new Customer();
        customer.setEmail("test@example.com");

        when(passwordDecoder.decodePassword(newPassword)).thenReturn("decodedPassword");
        when(otpRedisHelper.getOtp(mobileNumber)).thenReturn(otpDB);
        when(authDynamoRepository.getUserByMobileNumber(eq(mobileNumber), anyList())).thenReturn(customer);

        boolean result = passwordService.updatePassword(newPassword, mobileNumber, keyAndHash);

        assertFalse(result);
    }

    @Test
    void testUpdatePasswordDecodeException() {
        String newPassword = "newPassword";
        String mobileNumber = "1234567890";
        String keyAndHash = "key=clientID:hash=hashValue";

        when(passwordDecoder.decodePassword(newPassword)).thenThrow(new RuntimeException("Decode error"));

        assertThrows(RuntimeException.class, () -> passwordService.updatePassword(newPassword, mobileNumber, keyAndHash));
    }
    @Test
    void whenHashKeyAndClientIDMatch_thenUpdatePasswordAndReturnTrue() throws Exception{
        String mobileNumber = "1234567890";
        String newPassword = "password";
        String keyAndHash = "key=clientID:hashValue";
        String clientID = "clientID";
        String hashValue = "hashValue";
        String encodedPassword = null;

        when(otpDB.getHashKey()).thenReturn(hashValue);
        when(otpDB.getClientID()).thenReturn(clientID);
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        when(otpRedisHelper.getOtp(mobileNumber)).thenReturn(otpDB);  // Mock the otpRedisHelper.getOtp(mobileNumber) call to return otpDB

        boolean result = passwordService.updatePassword(newPassword, mobileNumber, keyAndHash);
    }
    @Test
    public void testUpdatePasswordThrowsRedisConnectionException() throws IOException {
        // Setup
        String newPassword = "newPassword";
        String mobileNumber = "1234567890";
        String keyAndHash = "key=clientID:hashValue";

        when(otpRedisHelper.getOtp(anyString())).thenThrow(new RedisConnectionException("Redis Connection Exception"));

        // Assert
        Assertions.assertThrows(RedisConnectionException.class, () -> {
            // Exercise
            passwordService.updatePassword(newPassword, mobileNumber, keyAndHash);
        }, "Redis Connection Exception");
    }
}








