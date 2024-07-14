package com.excitel.repository;
/**
 * This repository class provides methods for interacting with user data in a DynamoDB table.
 * It leverages helper classes like {@link AuthQueryBuilder} and {@link DynamoToCustomerWrapper}
 * for building DynamoDB requests and converting data between DynamoDB format and {@link Customer} objects.
 *
 * @author (your name here) (if applicable)
 * @since (version of your application) (if applicable)
 */
import com.excitel.dynamodbsdkhelper.AuthQueryBuilder;
import com.excitel.dynamodbsdkhelper.DynamoToCustomerWrapper;
import com.excitel.exception.custom.CustomDynamoDbException;
import com.excitel.exception.custom.CustomerAlreadyExistsException;
import com.excitel.exception.custom.UserNotFoundException;
import com.excitel.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
@Service
public class AuthDynamoRepository {
    @Autowired//NOSONAR
    AuthQueryBuilder queryBuilder;
    @Autowired//NOSONAR
    DynamoDbClient dynamoDbClient;
    @Autowired//NOSONAR
    DynamoToCustomerWrapper wrapper;
    String noUserFound = "No user Found: ";
    String resourceNotFound = "Resource not found";
    String provisionalThrowPutExceeded = "Provisioned throughput exceeded: ";
    String dynamoDbException = "DynamoDB exception: ";
    /**
     * Retrieves a {@link Customer} object from DynamoDB based on the provided mobile number and projection expression.
     * This method uses the `queryBuilder` to construct a GetItemRequest and retrieves user data from DynamoDB.
     * It then uses the `wrapper` to convert the retrieved item to a Customer object.
     * If no user is found for the mobile number, a {@link UserNotFoundException} is thrown.
     *
     * @param mobileNumber The mobile number of the user to retrieve.
     * @param projectionExpression A list of attributes to be retrieved from DynamoDB (optional).
     * @return A {@link Customer} object containing user data or throws an exception if not found.
     * @throws UserNotFoundException If no user is found for the provided mobile number.
     * @throws CustomDynamoDbException A custom exception wrapping various underlying DynamoDB exceptions.
     */
    public Customer getUserByMobileNumber(String mobileNumber, List<String> projectionExpression){
        try {
            GetItemRequest getRequest = queryBuilder.getUserRequest(mobileNumber, projectionExpression);
            GetItemResponse dbResponse = dynamoDbClient.getItem(getRequest);
            if (!dbResponse.item().isEmpty()) return wrapper.mapToCustomer(dbResponse.item());
            else throw new UserNotFoundException(noUserFound);
        }catch (ResourceNotFoundException e) {
            throw new CustomDynamoDbException(resourceNotFound + e.getMessage());
        } catch (ProvisionedThroughputExceededException e) {
            throw new CustomDynamoDbException( provisionalThrowPutExceeded + e.getMessage());
        } catch (DynamoDbException e) {
            throw new CustomDynamoDbException( dynamoDbException+ e.getMessage());
        }
    }
    /**
     * Updates user data in DynamoDB based on the provided {@link Customer} object.
     * This method uses the `queryBuilder` to construct an UpdateItemRequest and updates user data in DynamoDB.
     * If a resource is not found or there's a provisioned throughput issue, a {@link CustomDynamoDbException} is thrown.
     *
     * @param user A {@link Customer} object containing updated user data.
     * @return The updated {@link Customer} object on success.
     * @throws CustomDynamoDbException A custom exception wrapping various underlying DynamoDB exceptions.
     */
    public Customer updateUserByMobileNumber(Customer user){
        try {
            UpdateItemRequest request = queryBuilder.userUpdateRequest(user);
            dynamoDbClient.updateItem(request);
            return user;
        }catch (ResourceNotFoundException e) {
            throw new CustomDynamoDbException(resourceNotFound + e.getMessage());
        } catch (ProvisionedThroughputExceededException e) {
            throw new CustomDynamoDbException(provisionalThrowPutExceeded + e.getMessage());
        } catch (DynamoDbException e) {
            throw new CustomDynamoDbException(dynamoDbException + e.getMessage());
        } catch (Exception e){
            throw new CustomDynamoDbException(e.getMessage());
        }
    }
    /**
     * Saves a new user represented by a {@link Customer} object to DynamoDB.
     * This method uses the `queryBuilder` to construct a PutItemRequest and inserts the user data into DynamoDB.
     *
     * @param user A {@link Customer} object containing new user data.
     * @return True if the user is saved successfully, throws an exception otherwise.
     * @throws CustomDynamoDbException A custom exception wrapping various underlying DynamoDB exceptions
     *         including resource not found, provisioned throughput exceeded, and user already exists.
     */
    public boolean saveUser(Customer user) {
        try {
            PutItemRequest request = queryBuilder.userCreateRequest(user);
            PutItemResponse dbResponse = dynamoDbClient.putItem(request);//NOSONAR
            return true;
        } catch (ResourceNotFoundException e) {
            throw new CustomDynamoDbException("Resource not found");
        } catch (ProvisionedThroughputExceededException e) {
            throw new CustomDynamoDbException("Server Down");
        }catch (DynamoDbException e) {
            throw new CustomerAlreadyExistsException("user already exists");
        } catch (Exception e){
            throw new CustomDynamoDbException(e.getMessage());
        }
    }

    /**
     * Updates the password of a user in DynamoDB based on the provided mobile number and new encrypted password.
     * This method uses the `queryBuilder` to construct an UpdateItemRequest and updates the "Password" attribute
     * for the user identified by the mobile number.
     *
     * @param mobileNumber The mobile number of the user for whom to reset the password.
     * @param newEncryptedPassword The new encrypted password for the user.
     * @return True if the password is updated successfully, throws an exception otherwise.
     * @throws CustomDynamoDbException A custom exception wrapping various underlying DynamoDB exceptions
     *         including resource not found and DynamoDB exceptions.
     */
    public boolean updatePassword(String mobileNumber, String newEncryptedPassword){
        try{
            UpdateItemRequest request = queryBuilder.passwordResetRequest(mobileNumber, newEncryptedPassword);
            UpdateItemResponse dbResponse = dynamoDbClient.updateItem(request);//NOSONAR
            return true;
        }catch (ResourceNotFoundException e) {
            throw new CustomDynamoDbException(resourceNotFound + e.getMessage());
        }catch (DynamoDbException e) {
            throw new CustomDynamoDbException(dynamoDbException + e.getMessage());
        } catch (Exception e){
            throw new CustomDynamoDbException(e.getMessage());
        }
    }
}
