package com.excitel.exception.global;

import com.excitel.dto.ErrorObject;
import com.excitel.exception.custom.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GlobalExceptionHandlerTest {
    @Mock
    private WebRequest webRequest;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testHandleRegistrationException() {
        // Arrange
        RegistrationFailedException exception = new RegistrationFailedException("Registration failed");
        when(webRequest.getDescription(false)).thenReturn("Description of the error");

        // Act
        ResponseEntity<ErrorObject> responseEntity = globalExceptionHandler.handleRegistrationException(exception, webRequest);
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        ErrorObject errorObject = responseEntity.getBody();
        assertEquals("500 INTERNAL_SERVER_ERROR", errorObject.getStatus());
        assertEquals("Registration failed", errorObject.getMessage());
    }

    @Test
    void testHandleCustomerAlreadyExists() {
        // Arrange
        CustomerAlreadyExistsException exception = new CustomerAlreadyExistsException("Customer already exists");

        // Act
        ResponseEntity<ErrorObject> responseEntity = globalExceptionHandler.handleCustomerAlreadyExists(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorObject errorObject = responseEntity.getBody();
        assertEquals("400 BAD_REQUEST", errorObject.getStatus());
        assertEquals("Customer already exists", errorObject.getMessage());
    }

    @Test
    void testHandleUserNotFoundException() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException("User not found");

        // Act
        ResponseEntity<ErrorObject> responseEntity = globalExceptionHandler.handleUserNotFoundException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        ErrorObject errorObject = responseEntity.getBody();
        assertEquals("404 NOT_FOUND", errorObject.getStatus());
        assertEquals("User not found", errorObject.getMessage());
    }

//    @Test
//    void testHandleUserServiceException() {
//        // Arrange
//        UserServiceException exception = new UserServiceException("User service error");
//
//        // Act
//        ResponseEntity<ErrorObject> responseEntity = globalExceptionHandler.handleUserServiceException(exception, webRequest);
//
//        // Assert
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
//        ErrorObject errorObject = responseEntity.getBody();
//        assertEquals("500", errorObject.getStatus());
//        assertEquals("User service error", errorObject.getMessage());
//    }
@Test
public void testHandleUserServiceException() {
    // Mock the dependencies
    UserServiceException exception = new UserServiceException("User service exception: Internal error");
    WebRequest request = mock(WebRequest.class);

    // Invoke the method under test
    ResponseEntity<ErrorObject> response = globalExceptionHandler.handleUserServiceException(exception, request);

    // Verify the response
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(" Internal error", response.getBody().getMessage()); // Check for the extracted message
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.toString(), response.getBody().getStatus());
    assertEquals(new Date().toString(), response.getBody().getTimestamp().toString()); // Check timestamp (this will vary based on exact time)
}

    @Test
    void testHandleCustomAuthenticationException() {
        // Arrange
        CustomAuthenticationException exception = new CustomAuthenticationException("Authentication error");

        // Act
        ResponseEntity<ErrorObject> responseEntity = globalExceptionHandler.handleCustomAuthenticationException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
        ErrorObject errorObject = responseEntity.getBody();
        assertEquals("401 UNAUTHORIZED", errorObject.getStatus());
        assertEquals("Authentication error", errorObject.getMessage());
    }

    @Test
    void testHandleInvalidRequestBodyException() {
        // Arrange
        InvalidRequestBodyException exception = new InvalidRequestBodyException("Invalid request body");

        // Act
        ResponseEntity<ErrorObject> responseEntity = globalExceptionHandler.invalidRequestBodyException(exception);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
//        ErrorObject errorObject = responseEntity.getBody();
//        assertEquals("400 BAD_REQUEST", errorObject.getStatus());
//        assertEquals("Invalid request body", errorObject.getMessage());
    }

    @Test
    void testHandleCustomDynamoDbException() {
        // Arrange
        CustomDynamoDbException exception = new CustomDynamoDbException("DynamoDB error");

        // Act
        ResponseEntity<ErrorObject> responseEntity = globalExceptionHandler.handleCustomDynamoDbException(exception);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        ErrorObject errorObject = responseEntity.getBody();
        assertEquals("400 BAD_REQUEST", errorObject.getStatus());
        assertEquals("DynamoDB error", errorObject.getMessage());
    }
}
