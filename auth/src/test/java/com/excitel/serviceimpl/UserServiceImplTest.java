package com.excitel.serviceimpl;

import com.excitel.dto.FeignResponseDtO;
import com.excitel.dto.ResponseDTO;
import com.excitel.encryption.PasswordDecoder;
import com.excitel.exception.custom.CustomAuthenticationException;
import com.excitel.exception.custom.UserNotFoundException;
import com.excitel.exception.custom.UserServiceException;
import com.excitel.model.Customer;
import com.excitel.repository.AuthDynamoRepository;
import com.excitel.redishelper.AuthRedis;
import com.excitel.security.JwtHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.management.ServiceNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private AuthDynamoRepository authDynamoRepository;

    @Mock
    private AuthRedis authRedis;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private PasswordDecoder passwordDecoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidation_CatchException() {
        // Mock the dependencies and method behavior
        String token = "valid_token";
        String mobileNumber = "1234567890";

        // Mock behavior of jwtHelper and authDynamoRepository
        when(jwtHelper.extractMobileNumberFromToken(anyString())).thenReturn(mobileNumber);
        when(jwtHelper.extractRoleFromToken(anyString())).thenReturn("ROLE_USER"); // Mocking role extraction
        when(authDynamoRepository.getUserByMobileNumber(mobileNumber, Arrays.asList("MobileNumber", "Email", "PlanType","SIMType")))
                .thenThrow(new RuntimeException("Simulated DynamoDB Exception"));

        // Perform the test
        Exception exception = assertThrows(CustomAuthenticationException.class, () -> {
            userService.validation(token);
        });

        // Verify expected behavior
        assertEquals("Simulated DynamoDB Exception", exception.getMessage());
    }

    @Test
    public void testValidationException() {
        // Mock data
        String token = "invalidToken";

        // Mock behavior of dependencies
        when(jwtHelper.extractMobileNumberFromToken(any())).thenThrow(new CustomAuthenticationException("Invalid token"));

        // Test validation method and expect an exception
        assertThrows(CustomAuthenticationException.class, () -> userService.validation(token));

        // Verify interactions
        verify(jwtHelper).extractMobileNumberFromToken(any());
        verifyNoInteractions(authRedis);
        verifyNoInteractions(authDynamoRepository);
    }
    @Test
    public void testValidation_TokenInvalidated() throws ServiceNotFoundException {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // Prepare test data
        String token = "invalidToken";

        // Mock behavior of JwtHelper.isTokenInvalidated
        when(jwtHelper.isTokenInvalidated(anyString())).thenReturn(true);

        // Verify that UserNotFoundException is thrown with the expected message
        assertThrows(UserNotFoundException.class, () -> userService.validation(token));

    }
    @Test
    public void testGetUserDetail() throws ServiceNotFoundException {
        // Mock data
        String token = "validToken";
        String mobileNumber = "1234567890";
        Customer mockedCustomer = new Customer();
        mockedCustomer.setName("John Doe");
        mockedCustomer.setEmail("john@example.com");
        mockedCustomer.setRole("user");
        mockedCustomer.setSimType("prepaid");

        // Mock behavior of dependencies
        when(jwtHelper.extractMobileNumberFromToken(any())).thenReturn(mobileNumber);
        when(authRedis.getUserCacheDetail(any())).thenReturn(null); // Simulate cache miss
        when(authDynamoRepository.getUserByMobileNumber(any(), any())).thenReturn(mockedCustomer);

        // Test getUserDetail method
        Customer result = userService.getUserDetail(token);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("user", result.getRole());

        // Verify interactions
        verify(authRedis).getUserCacheDetail(mobileNumber);
        verify(authDynamoRepository).getUserByMobileNumber(mobileNumber,
                Arrays.asList("MobileNumber", "Email", "SIMType", "UserRole", "Address", "PINCode", "Country", "CustomerName", "CustomerState"));
        verify(authRedis).addUserDetailsCache(mobileNumber, mockedCustomer);
    }
    @Test
    public void testUpdateUserDetail() {
        // Mock data
        Customer customerToUpdate = new Customer();
        customerToUpdate.setName("John Doe");
        customerToUpdate.setEmail("john@example.com");
        customerToUpdate.setRole("user");
        customerToUpdate.setSimType("prepaid");

        // Mock behavior of dependencies
        when(authDynamoRepository.updateUserByMobileNumber(any())).thenReturn(customerToUpdate);

        // Test updateUserDetail method
        Customer updatedCustomer = userService.updateUserDetail(customerToUpdate);

        assertNotNull(updatedCustomer);
        assertEquals("John Doe", updatedCustomer.getName());
        assertEquals("user", updatedCustomer.getRole());

        // Verify interactions
        verify(authRedis).removeUserDetailsCache(customerToUpdate.getMobileNumber());
        verify(authDynamoRepository).updateUserByMobileNumber(customerToUpdate);
    }
    @Test
    public void testLoadUserByUsername() {
        // Create a UserServiceImpl instance
        UserServiceImpl userService = new UserServiceImpl();

        // Test the loadUserByUsername method
        String username = "test_username";
        Customer customer = userService.loadUserByUsername(username);

        // Verify that the repository method was not called and the result is null
        assertNull(customer);
    }
    @Test
    public void testConvertDtOJWT() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // Prepare test data
        HttpStatus status = HttpStatus.OK;
        String message = "Success";
        String token = "xyz123";

        // Create an instance of UserServiceImpl
        UserServiceImpl userService = new UserServiceImpl();

        // Get the private method convertDtOJWT using reflection
        Method convertDtOJWTMethod = UserServiceImpl.class.getDeclaredMethod("convertDtOJWT", HttpStatus.class, String.class, String.class);
        convertDtOJWTMethod.setAccessible(true); // Set accessible to invoke private method

        // Invoke the private method with the test data
        ResponseDTO responseDTO = (ResponseDTO) convertDtOJWTMethod.invoke(userService, status, message, token);

        // Verify the ResponseDTO object returned by the method
        assertNotNull(responseDTO);
        assertEquals(HttpStatus.OK, responseDTO.getStatus());
        assertEquals("Success", responseDTO.getMessage());
        assertEquals("xyz123", responseDTO.getResponse());
    }
    @Test
    void testLogout_ValidToken() throws ServiceNotFoundException {
        String token = "valid_token";

        // Mock jwtHelper.isTokenInvalidated to return false (valid token)
        when(jwtHelper.isTokenInvalidated(token.substring(7))).thenReturn(false);

        // Call the method under test
        ResponseEntity<ResponseDTO> responseEntity = userService.logout(token);

        // Verify the response
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("valid User", responseEntity.getBody().getMessage());
    }

    @Test
    void testLogout_InvalidToken() throws ServiceNotFoundException {
        String token = "invalid_token";

        // Mock jwtHelper.isTokenInvalidated to return true (invalid token)
        when(jwtHelper.isTokenInvalidated(token.substring(7))).thenReturn(true);

        // Call the method under test
        ResponseEntity<ResponseDTO> responseEntity = userService.logout(token);

        // Verify the response
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Invalid Auth Token", responseEntity.getBody().getMessage());
    }

    @Test
    void testLogout_ExceptionHandling() throws ServiceNotFoundException {
        String token = "invalid_token";

        // Mock jwtHelper.isTokenInvalidated to throw UserNotFoundException
        when(jwtHelper.isTokenInvalidated(token.substring(7))).thenThrow(new UserNotFoundException("Invalid token"));

        // Call the method under test and verify exception handling
        try {
            userService.logout(token);
        } catch (UserNotFoundException e) {
            assertEquals("Invalid token", e.getMessage());
        }
    }
    @Test
    void testValidation_ValidToken_NoCachedDetails() throws ServiceNotFoundException {
        // Mock data
        String token = "Bearer validToken";
        String mobileNumber = "1234567890";
        Customer mockedCustomer = new Customer();
        mockedCustomer.setEmail("john@example.com");
        mockedCustomer.setSimType("prepaid");

        // Mock behavior of dependencies
        when(jwtHelper.extractMobileNumberFromToken(any())).thenReturn(mobileNumber);
        when(jwtHelper.isTokenInvalidated(anyString())).thenReturn(false);
        when(authRedis.getUserCacheDetail(any())).thenReturn(null); // Simulate cache miss
        when(authDynamoRepository.getUserByMobileNumber(any(), any())).thenReturn(mockedCustomer);
        when(jwtHelper.extractRoleFromToken(anyString())).thenReturn("ROLE_USER"); // Mock valid role extraction

        // Test validation method
        ResponseEntity<FeignResponseDtO> response = userService.validation(token);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User authorised", response.getBody().getMessage());
        assertEquals(mobileNumber, response.getBody().getMobileNumber());
        assertEquals("john@example.com", response.getBody().getEmail());
        assertEquals("USER", response.getBody().getRole()); // Adjust based on your role extraction logic
        assertEquals("prepaid", response.getBody().getSimType());

        // Verify interactions
        verify(authRedis).getUserCacheDetail(mobileNumber);
        verify(authDynamoRepository).getUserByMobileNumber(mobileNumber, Arrays.asList("MobileNumber", "Email", "PlanType","SIMType"));
        verify(jwtHelper).extractRoleFromToken(token.substring(7)); // Verify role extraction from token
    }
    @Test
    public void validationTest_CachedUser() throws ServiceNotFoundException {
        // Arrange
        String token = "Bearer validToken";
        String mobileNumber = "1234567890";
        String email = "test@example.com";
        String role = "UserRole";
        String simType = "SimType";
        Customer cacheDetail = new Customer();
        cacheDetail.setEmail(email);
        cacheDetail.setSimType(simType);
        FeignResponseDtO cacheResponse = new FeignResponseDtO(
                HttpStatus.OK,
                "User authorised",
                mobileNumber,
                email,
                role,
                simType);

        when(jwtHelper.isTokenInvalidated("validToken")).thenReturn(false);
        when(jwtHelper.extractMobileNumberFromToken("validToken")).thenReturn(mobileNumber);
        when(jwtHelper.extractRoleFromToken("validToken")).thenReturn("ROLE_" + role);
        when(authRedis.getUserCacheDetail(mobileNumber)).thenReturn(cacheDetail);

        // Act
        ResponseEntity<FeignResponseDtO> result = userService.validation(token);

        // Assert
//        assertEquals(ResponseEntity.status(HttpStatus.OK).body(cacheResponse), result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(jwtHelper, times(1)).isTokenInvalidated("validToken");
        verify(jwtHelper, times(1)).extractMobileNumberFromToken("validToken");
        verify(jwtHelper, times(1)).extractRoleFromToken("validToken");
        verify(authRedis, times(1)).getUserCacheDetail(mobileNumber);
    }
    @Test
    void testResetPassword() throws ServiceNotFoundException {
        String token = "Bearer yourToken";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(passwordDecoder.decodePassword(oldPassword)).thenReturn("decodedOldPassword");
        when(passwordDecoder.decodePassword(newPassword)).thenReturn("decodedNewPassword");
        when(jwtHelper.extractMobileNumberFromToken(anyString())).thenReturn("1234567890");
        when(jwtHelper.isTokenInvalidated(anyString())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedNewPassword");

        boolean result = userService.resetPassword(token, oldPassword, newPassword);

        assertTrue(result);
        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(authDynamoRepository).updatePassword(anyString(), anyString());

    }

    @Test
    void testResetPasswordInvalidToken() throws ServiceNotFoundException {
        String token = "Bearer yourToken";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(jwtHelper.isTokenInvalidated(anyString())).thenReturn(true);

        assertThrows(UserNotFoundException.class, () -> userService.resetPassword(token, oldPassword, newPassword));
    }

    @Test
    void testResetPasswordAuthenticationException() throws ServiceNotFoundException {
        String token = "Bearer yourToken";
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        when(passwordDecoder.decodePassword(oldPassword)).thenReturn("decodedOldPassword");
        when(passwordDecoder.decodePassword(newPassword)).thenReturn("decodedNewPassword");
        when(jwtHelper.extractMobileNumberFromToken(anyString())).thenReturn("1234567890");
        when(jwtHelper.isTokenInvalidated(anyString())).thenReturn(false);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedNewPassword");
        doThrow(new AuthenticationException("Authentication failed") {}).when(authenticationManager).authenticate(any(Authentication.class));

        assertThrows(CustomAuthenticationException.class, () -> userService.resetPassword(token, oldPassword, newPassword));
    }

    @Test
    public void testLoginThrowsException() {
        String mobileNumber = "1234567890";
        String password = "password";

        when(passwordDecoder.decodePassword(password)).thenThrow(new CustomAuthenticationException("Error"));

        assertThrows(CustomAuthenticationException.class, () -> userService.login(mobileNumber, password));
    }
    @Test
    public void testLogin_Failure() {
        // Arrange
        String mobileNumber = "1234567890";
        String password = "password";

        when(passwordDecoder.decodePassword(password)).thenThrow(new AuthenticationException("Invalid password") {});

        // Act & Assert
        assertThrows(CustomAuthenticationException.class, () -> userService.login(mobileNumber, password));
        verify(passwordDecoder, times(1)).decodePassword(password);
    }
    @Test
    public void authenticateUserTest() {
        // Arrange
        String mobileNumber = "1234567890";
        String password = "password";
        String role = "UserRole";
        String token = "token";
        Authentication authentication = new UsernamePasswordAuthenticationToken(mobileNumber, password);
        Customer user = new Customer();
        user.setRole(role);
        UserDetails userDetails = new User(mobileNumber, "", Collections.emptyList());

        when(authenticationManager.authenticate(authentication)).thenReturn(authentication);
        when(authDynamoRepository.getUserByMobileNumber(mobileNumber, Arrays.asList("SIMType", "Email", "UserRole"))).thenReturn(user);
        when(jwtHelper.generateToken(userDetails, mobileNumber, role)).thenReturn(token);

        // Act
        ResponseEntity<ResponseDTO> result = userService.authenticateUser(mobileNumber, password);

        // Assert
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(authenticationManager, times(1)).authenticate(authentication);
        verify(authDynamoRepository, times(1)).getUserByMobileNumber(mobileNumber, Arrays.asList("SIMType", "Email", "UserRole"));
        verify(jwtHelper, times(1)).generateToken(userDetails, mobileNumber, role);
    }
    @Test
    public void logoutTest_UserNotFoundException() throws ServiceNotFoundException {
        // Arrange
        String token = "Bearer token";
        String mobileNumber = "1234567890";

        when(jwtHelper.extractMobileNumberFromToken(token.substring(7))).thenReturn(mobileNumber);
        when(jwtHelper.isTokenInvalidated(token.substring(7))).thenReturn(false);
        doThrow(new UserNotFoundException("User not found")).when(jwtHelper).invalidateToken(token.substring(7));

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> userService.logout(token));
    }

    @Test
    public void logoutTest_UserServiceException() throws ServiceNotFoundException {
        // Arrange
        String token = "Bearer token";
        String mobileNumber = "1234567890";

        when(jwtHelper.extractMobileNumberFromToken(token.substring(7))).thenReturn(mobileNumber);
        when(jwtHelper.isTokenInvalidated(token.substring(7))).thenReturn(false);
        doThrow(new UserServiceException("Internal server error")).when(authRedis).removeUserDetailsCache(mobileNumber);

        // Act & Assert
        assertThrows(UserServiceException.class, () -> userService.logout(token));
        verify(jwtHelper, times(1)).invalidateToken(token.substring(7));
    }
    @Test
    void whenTokenIsInvalidated_thenUserNotFoundExceptionIsThrown() throws ServiceNotFoundException {
        String token = "Bearer invalidToken";

        when(jwtHelper.isTokenInvalidated(token.substring(7))).thenReturn(true);

        assertThrows(UserNotFoundException.class, () -> userService.getUserDetail(token));
    }

    @Test
    void whenTokenIsValid_thenUserNotFoundExceptionIsNotThrown() throws ServiceNotFoundException {
        String token = "Bearer validToken";

        when(jwtHelper.isTokenInvalidated(token.substring(7))).thenReturn(false);

        assertDoesNotThrow(() -> userService.getUserDetail(token));
    }
}

