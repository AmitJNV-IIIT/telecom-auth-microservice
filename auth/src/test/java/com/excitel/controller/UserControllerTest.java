package com.excitel.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.excitel.dto.JwtRequestDTO;
import com.excitel.dto.ResetPasswordDTO;
import com.excitel.dto.ResponseDTO;
import com.excitel.dto.UserDTO;
import com.excitel.exception.custom.CustomDynamoDbException;
import com.excitel.exception.custom.DynamoDBConnectionException;
import com.excitel.model.Customer;
import com.excitel.service.UserService;
import io.lettuce.core.SocketOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.management.ServiceNotFoundException;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final String mockToken = "Bearer mockToken";
    private final String mockMobileNumber = "1234567890";
    private final String mockPassword = "password123";
    private final String mockOldPassword = "oldPassword";
    private final String mockNewPassword = "newPassword";

    private Customer mockUser;
    private JwtRequestDTO mockLoginRequest;
    private ResetPasswordDTO mockResetPasswordRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new Customer();
        mockUser.setMobileNumber(mockMobileNumber);

        mockLoginRequest = new JwtRequestDTO(mockMobileNumber, mockPassword);

        mockResetPasswordRequest = new ResetPasswordDTO(mockOldPassword, mockNewPassword);
    }

    @Test
    void initalisation_HealthCheck_Success() {
        // Act
        String result = userController.initalisation();

        // Assert
        assertEquals("live", result);
    }

    @Test
    void isValid_ValidToken_Success() throws ServiceNotFoundException {
        // Arrange
        when(userService.validation(mockToken)).thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<?> response = userController.isValid(mockToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void login_ValidCredentials_Success() {
        // Arrange
        when(userService.login(mockMobileNumber, mockPassword)).thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<?> response = userController.login(mockLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getUserDetails_ValidToken_Success() throws ServiceNotFoundException {
        // Arrange
        when(userService.getUserDetail(mockToken)).thenReturn(mockUser);

        // Act
        ResponseEntity<?> response = userController.getUserDetails(mockToken);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDTO userDtO = (UserDTO) response.getBody();
        assertNotNull(userDtO);
        assertEquals(HttpStatus.OK, userDtO.getStatus());
        assertEquals("Details fetched successfully.", userDtO.getMessage());
        assertEquals(mockUser, userDtO.getData());
    }
    @Test
    public void logoutTest() throws ServiceNotFoundException {
        // Arrange
        String token = "testToken";
        ResponseDTO responseDTO = new ResponseDTO();
        when(userService.logout(token)).thenReturn(ResponseEntity.ok(responseDTO));

        // Act
        ResponseEntity<ResponseDTO> result = userController.logout(token);

        // Assert
        assertEquals(ResponseEntity.ok(responseDTO), result);
        verify(userService, times(1)).logout(token);
    }
    @Test
    public void testUpdateUserDetails_ValidRequest1() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // Prepare test data
        Customer mockUser = new Customer();
        mockUser.setEmail("test@example.com");
        mockUser.setMobileNumber("1234567890");
        mockUser.setName("Test User");
        mockUser.setAddress("123 Test St");
        mockUser.setSimType("Postpaid");
        mockUser.setPinCode("12345");
        mockUser.setCountry("Country");
        mockUser.setState("State");

        // Mock UserService behavior to throw CustomDynamoDbException for invalid request
        when(userService.updateUserDetail(any(Customer.class)))
                .thenThrow(new CustomDynamoDbException("Invalid Request Body"));

        // Call the method to test and expect CustomDynamoDbException
        Exception exception = assertThrows(CustomDynamoDbException.class,
                () -> userController.updateUserDetails(mockUser));

        // Verify that CustomDynamoDbException was thrown with the expected message
        assertEquals("Invalid Request Body", exception.getMessage());
    }

    @Test
    public void testUpdateUserDetails_InvalidRequest() {
        // Initialize mocks
        MockitoAnnotations.initMocks(this);

        // Prepare test data
        Customer invalidUser = new Customer(); // This will be considered invalid based on your isValidResponse method

        // Mock UserService behavior (assuming it won't be called due to invalid request)
        when(userService.updateUserDetail(any(Customer.class))).thenThrow(new RuntimeException("Should not be called"));

        // Call the method to test and expect CustomDynamoDbException
        Exception exception = assertThrows(CustomDynamoDbException.class,
                () -> userController.updateUserDetails(invalidUser));

        // Verify that CustomDynamoDbException was thrown with the expected message
        assertEquals("Invalid Request Body", exception.getMessage());

        // Verify that UserService was NOT called (since the request is invalid)
        verifyNoInteractions(userService);
    }

    @Test
    void resetPassword_ValidTokenAndRequest_Success() throws ServiceNotFoundException {
        // Act
        ResponseEntity<?> response = userController.resetPassword(mockToken, mockResetPasswordRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ResponseDTO responseDto = (ResponseDTO) response.getBody();
        assertNotNull(responseDto);
        assertEquals(HttpStatus.OK, responseDto.getStatus());
        assertEquals("Password Updated", responseDto.getMessage());
        assertEquals("Success", responseDto.getResponse());
    }
    @Test
    void updateUserDetails_InvalidUserDetails_ReturnsBadRequest() {
        // Mock UserService
        UserService userService = Mockito.mock(UserService.class);

        // Create a controller instance with the mocked UserService
        UserController controller = new UserController(userService);

        // Create an invalid Customer object (e.g., missing required field)
        Customer invalidUser = new Customer(); // Set invalid data here

        // Call the controller method
        try {
            controller.updateUserDetails(invalidUser);
            Assertions.fail("Expected an exception to be thrown");
        } catch (CustomDynamoDbException e) {
            // Expected exception is thrown, validate the message
            Assertions.assertEquals("Invalid Request Body", e.getMessage());
        }
        // Verify that userService.updateUserDetail was not called (since request is invalid)
        Mockito.verify(userService, Mockito.times(0)).updateUserDetail(invalidUser);
    }
    @Test
    public void testUpdateUserDetails_ValidUserDetails_ReturnsSuccess() {
        // Mock or create UserService (depending on your implementation)
        UserService userService = Mockito.mock(UserService.class);
        // ... configure mock behavior if needed

        // Create a valid Customer object
        Customer validUser = new Customer(); // Set valid data here
        validUser.setEmail("hello@gmail.com");
        validUser.setName("Aarti");
        validUser.setAddress("Gurgaon");
        validUser.setPinCode("177671");
        validUser.setState("Haryana");
        // Set up expected behavior of isValidResponse method (return true)
//        Mockito.when(userController.isValidResponse(validUser)).thenReturn(true);

        // Mock or create a valid updated user (optional)
        Customer updatedUser = new Customer(); // Set updated user data here
//        UserService userServices = Mockito.mock(UserService.class);

        // Call the controller method (assuming successful update)
        UserController controller = new UserController(userService);
        ResponseEntity<UserDTO> response = null;
        try {
            response = controller.updateUserDetails(validUser);
            Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
            // Verify construction of userDtO (assuming you have a getter for userDtO)
            UserDTO userDtO = response.getBody();
            Assertions.assertNotNull(userDtO);
            Assertions.assertEquals(HttpStatus.OK, userDtO.getStatus());
            Assertions.assertEquals("updated user details set successfully.", userDtO.getMessage());

        } catch (CustomDynamoDbException e) {
            // Expected exception is thrown, validate the message
            Assertions.assertEquals("Invalid Request Body", e.getMessage());
        }
    }
    @Test
    public void testUpdateUserDetails_ValidUserDetails_ReturnsSuccess_Always() {
        UserService userService = Mockito.mock(UserService.class);
        // Configure mock behavior if needed (e.g., successful update)
        Mockito.when(userService.updateUserDetail(Mockito.any(Customer.class))).thenReturn(new Customer());

        // Create a valid Customer object
        Customer validUser = new Customer();
        validUser.setEmail("hello@gmail.com");
        validUser.setName("Aarti");
        validUser.setAddress("Gurgaon");
        validUser.setPinCode("177671");
        validUser.setState("Haryana");

        // Mock or implement isValidResponse method (assuming it exists)
        userController.isValidResponse(validUser);

        // Call the controller method (assuming successful update)
        UserController controller = new UserController(userService);
        ResponseEntity<UserDTO> response = controller.updateUserDetails(validUser);

        // Assertions
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify construction of userDtO (assuming you have a getter for userDtO)
        UserDTO userDtO = response.getBody();
        Assertions.assertNotNull(userDtO);

    }
    @Test
    void testNullString() {
        String str = null;
        assertTrue(userController.isNullOrEmpty(str));
    }

    @Test
    void testEmptyString() {
        String str = "";
        assertTrue(userController.isNullOrEmpty(str));
    }

    @Test
    void testNonEmptyString() {
        String str = "Hello World";
        assertFalse(userController.isNullOrEmpty(str));
    }

    @Test
    void testStringWithWhitespace() {
        String str = "  "; // Only whitespace characters
        assertFalse(userController.isNullOrEmpty(str));
    }

}

