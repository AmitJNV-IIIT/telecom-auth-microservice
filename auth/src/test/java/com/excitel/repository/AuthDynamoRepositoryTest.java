package com.excitel.repository;
import com.amazonaws.SdkClientException;
import com.excitel.dynamodbsdkhelper.AuthQueryBuilder;
import com.excitel.dynamodbsdkhelper.DynamoToCustomerWrapper;
import com.excitel.exception.custom.CustomDynamoDbException;
import com.excitel.exception.custom.CustomerAlreadyExistsException;
import com.excitel.exception.custom.UserNotFoundException;
import com.excitel.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthDynamoRepositoryTest {

    private AuthDynamoRepository repository;

    @Mock
    private AuthQueryBuilder mockQueryBuilder;

    @Mock
    private DynamoDbClient mockDynamoDbClient;

    @Mock
    private DynamoToCustomerWrapper mockWrapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new AuthDynamoRepository();
        repository.queryBuilder = mockQueryBuilder;
        repository.dynamoDbClient = mockDynamoDbClient;
        repository.wrapper = mockWrapper;
    }

    @Test
    void getUserByMobileNumber_Successful() {
        // Arrange
        String mobileNumber = "1234567890";
        List<String> projectionExpression = new ArrayList<>();
        GetItemRequest getRequest = GetItemRequest.builder().build();
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("MobileNumber", AttributeValue.builder().s(mobileNumber).build());
        GetItemResponse getItemResponse = GetItemResponse.builder().item(item).build();
        when(mockQueryBuilder.getUserRequest(mobileNumber, projectionExpression)).thenReturn(getRequest);
        when(mockDynamoDbClient.getItem(getRequest)).thenReturn(getItemResponse);

        // Act
        repository.getUserByMobileNumber(mobileNumber, projectionExpression);

        // Assert
        verify(mockWrapper, times(1)).mapToCustomer(item);
    }

    @Test
    void getUserByMobileNumber_UserNotFoundException() {
        // Arrange
        String mobileNumber = "1234567890";
        List<String> projectionExpression = new ArrayList<>();
        GetItemRequest getRequest = GetItemRequest.builder().build();
        GetItemResponse getItemResponse = GetItemResponse.builder().item(null).build();
        when(mockQueryBuilder.getUserRequest(mobileNumber, projectionExpression)).thenReturn(getRequest);
        when(mockDynamoDbClient.getItem(getRequest)).thenReturn(getItemResponse);

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> repository.getUserByMobileNumber(mobileNumber, projectionExpression));
    }
    @Test
    void updateUserByMobileNumber_Successful() {
        // Arrange
        Customer user = new Customer();
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        UpdateItemResponse updateItemResponse = UpdateItemResponse.builder().build();
        when(mockQueryBuilder.userUpdateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenReturn(updateItemResponse);

        // Act
        Customer result = repository.updateUserByMobileNumber(user);

        // Assert
        assertEquals(user, result);
    }

    @Test
    void updateUserByMobileNumber_ResourceNotFoundException() {
        // Arrange
        Customer user = new Customer();
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        when(mockQueryBuilder.userUpdateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenThrow(ResourceNotFoundException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.updateUserByMobileNumber(user));
    }

// Similarly, write tests for ProvisionedThroughputExceededException, SdkClientException, and DynamoDbException

    @Test
    void saveUser_Successful() {
        // Arrange
        Customer user = new Customer();
        PutItemRequest request = PutItemRequest.builder().build();
        PutItemResponse putItemResponse = PutItemResponse.builder().build();
        when(mockQueryBuilder.userCreateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.putItem(request)).thenReturn(putItemResponse);

        // Act
        boolean result = repository.saveUser(user);

        // Assert
        assertTrue(result);
    }

    @Test
    void saveUser_ResourceNotFoundException() {
        // Arrange
        Customer user = new Customer();
        PutItemRequest request = PutItemRequest.builder().build();
        when(mockQueryBuilder.userCreateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.putItem(request)).thenThrow(ResourceNotFoundException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.saveUser(user));
    }

// Similarly, write tests for ProvisionedThroughputExceededException, SdkClientException, and DynamoDbException

    @Test
    void updatePassword_Successful() {
        // Arrange
        String mobileNumber = "1234567890";
        String newPassword = "newPassword";
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        UpdateItemResponse updateItemResponse = UpdateItemResponse.builder().build();
        when(mockQueryBuilder.passwordResetRequest(mobileNumber, newPassword)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenReturn(updateItemResponse);

        // Act
        boolean result = repository.updatePassword(mobileNumber, newPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void updatePassword_ResourceNotFoundException() {
        // Arrange
        String mobileNumber = "1234567890";
        String newPassword = "newPassword";
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        when(mockQueryBuilder.passwordResetRequest(mobileNumber, newPassword)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenThrow(ResourceNotFoundException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.updatePassword(mobileNumber, newPassword));
    }
    @Test
    void updateUserByMobileNumber_ProvisionedThroughputExceededException() {
        // Arrange
        Customer user = new Customer();
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        when(mockQueryBuilder.userUpdateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenThrow(ProvisionedThroughputExceededException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.updateUserByMobileNumber(user));
    }

    @Test
    void updateUserByMobileNumber_SdkClientException() {
        // Arrange
        Customer user = new Customer();
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        when(mockQueryBuilder.userUpdateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenThrow(SdkClientException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.updateUserByMobileNumber(user));
    }

    @Test
    void updateUserByMobileNumber_DynamoDbException() {
        // Arrange
        Customer user = new Customer();
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        when(mockQueryBuilder.userUpdateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenThrow(DynamoDbException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.updateUserByMobileNumber(user));
    }
    @Test
    void saveUser_ProvisionedThroughputExceededException() {
        // Arrange
        Customer user = new Customer();
        PutItemRequest request = PutItemRequest.builder().build();
        when(mockQueryBuilder.userCreateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.putItem(request)).thenThrow(ProvisionedThroughputExceededException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.saveUser(user));
    }

    @Test
    void saveUser_SdkClientException() {
        // Arrange
        Customer user = new Customer();
        PutItemRequest request = PutItemRequest.builder().build();
        when(mockQueryBuilder.userCreateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.putItem(request)).thenThrow(SdkClientException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.saveUser(user));
    }

    @Test
    void saveUser_DynamoDbException() {
        // Arrange
        Customer user = new Customer();
        PutItemRequest request = PutItemRequest.builder().build();
        when(mockQueryBuilder.userCreateRequest(user)).thenReturn(request);
        when(mockDynamoDbClient.putItem(request)).thenThrow(DynamoDbException.class);

        // Act and Assert
        assertThrows(CustomerAlreadyExistsException.class, () -> repository.saveUser(user));
    }

    @Test
    void updatePassword_ProvisionedThroughputExceededException() {
        // Arrange
        String mobileNumber = "1234567890";
        String newPassword = "newPassword";
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        when(mockQueryBuilder.passwordResetRequest(mobileNumber, newPassword)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenThrow(ProvisionedThroughputExceededException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.updatePassword(mobileNumber, newPassword));
    }

    @Test
    void updatePassword_SdkClientException() {
        // Arrange
        String mobileNumber = "1234567890";
        String newPassword = "newPassword";
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        when(mockQueryBuilder.passwordResetRequest(mobileNumber, newPassword)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenThrow(SdkClientException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.updatePassword(mobileNumber, newPassword));
    }

    @Test
    void updatePassword_DynamoDbException() {
        // Arrange
        String mobileNumber = "1234567890";
        String newPassword = "newPassword";
        UpdateItemRequest request = UpdateItemRequest.builder().build();
        when(mockQueryBuilder.passwordResetRequest(mobileNumber, newPassword)).thenReturn(request);
        when(mockDynamoDbClient.updateItem(request)).thenThrow(DynamoDbException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.updatePassword(mobileNumber, newPassword));
    }

    @Test
    void getUserByMobileNumber_ProvisionedThroughputExceededException() {
        // Arrange
        String mobileNumber = "1234567890";
        List<String> projectionExpression = new ArrayList<>();
        GetItemRequest getRequest = GetItemRequest.builder().build();
        when(mockQueryBuilder.getUserRequest(mobileNumber, projectionExpression)).thenReturn(getRequest);
        when(mockDynamoDbClient.getItem(getRequest)).thenThrow(ProvisionedThroughputExceededException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.getUserByMobileNumber(mobileNumber, projectionExpression));
    }
//    @Test
//    void getUserByMobileNumber_ResourceNotFoundException(){
//        // Arrange
//        String mobileNumber = "1234567890";
//
//        when(mockDynamoDbClient.getItem(getRequest)).thenThrow(ResourceNotFoundException.class);
//
//        assertThrows(CustomDynaoDbException.class, () -> repository.getUserByMobileNumber(mobileNumber, projectionExpression));
//    }
    @Test
    void getUserByMobileNumber_ResourceNotFoundException() {
        // Arrange
        String mobileNumber = "1234567890";
        List<String> projectionExpression = new ArrayList<>();
        GetItemRequest getRequest = GetItemRequest.builder().build();
        when(mockQueryBuilder.getUserRequest(mobileNumber, projectionExpression)).thenReturn(getRequest);
        when(mockDynamoDbClient.getItem(getRequest)).thenThrow(ResourceNotFoundException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.getUserByMobileNumber(mobileNumber, projectionExpression));
    }


//    @Test
//    void getUserByMobileNumber_SdkClientException() {
//        // Arrange
//        String mobileNumber = "1234567890";
//        List<String> projectionExpression = new ArrayList<>();
//        GetItemRequest getRequest = GetItemRequest.builder().build();
//        when(mockQueryBuilder.getUserRequest(mobileNumber, projectionExpression)).thenReturn(getRequest);
//        when(mockDynamoDbClient.getItem(getRequest)).thenThrow(SdkClientException.class);
//
//        // Act and Assert
//        assertThrows(CustomDynamoDbException.class, () -> repository.getUserByMobileNumber(mobileNumber, projectionExpression));
//    }

    @Test
    void getUserByMobileNumber_DynamoDbException() {
        // Arrange
        String mobileNumber = "1234567890";
        List<String> projectionExpression = new ArrayList<>();
        GetItemRequest getRequest = GetItemRequest.builder().build();
        when(mockQueryBuilder.getUserRequest(mobileNumber, projectionExpression)).thenReturn(getRequest);
        when(mockDynamoDbClient.getItem(getRequest)).thenThrow(DynamoDbException.class);

        // Act and Assert
        assertThrows(CustomDynamoDbException.class, () -> repository.getUserByMobileNumber(mobileNumber, projectionExpression));
    }

}