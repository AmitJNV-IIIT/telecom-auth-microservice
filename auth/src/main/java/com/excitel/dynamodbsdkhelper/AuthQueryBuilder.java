package com.excitel.dynamodbsdkhelper;

import com.excitel.model.Customer;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Service
public class AuthQueryBuilder {

    public static final String USER_NAME = "MobileNumber";
    public static final String TABLE = "customer-table";

    /**
     * Build a query request to retrieve user details based on mobile number.
     *
     * @param mobileNumber       The mobile number of the user.
     * @param projectionList     The list of attributes to project in the query result.
     * @return                   The constructed QueryRequest.
     */
    public QueryRequest mobileQqueryRequest(String mobileNumber, String tableName, List<String> projectionList){
        String projectionKey = String.join(", ", projectionList);
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":v", AttributeValue.builder().s(mobileNumber).build());

        return QueryRequest.builder()
                .projectionExpression(projectionKey)
                .tableName(tableName)
                .keyConditionExpression("MobileNumber = :v")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }
    /**
     * Build a query request to retrieve user details based on email.
     *
     * @param email              The email of the user.
     * @param projectionList     The list of attributes to project in the query result.
     * @return                   The constructed QueryRequest.
     */
    public QueryRequest emailQueryRequest(String email, String tableName, List<String> projectionList){
        String projectionKey = String.join(", ", projectionList);
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":v", AttributeValue.builder().s(email).build());

        return QueryRequest.builder()
                .projectionExpression(projectionKey)
                .indexName("Email-index")
                .tableName(tableName)
                .keyConditionExpression("Email = :v")
                .expressionAttributeValues(expressionAttributeValues)
                .build();
    }
    /**
     * Build a GetItemRequest to retrieve user details based on mobile number.
     *
     * @param mobileNumber         The mobile number of the user.
     * @param projectionExpression The list of attributes to project in the query result.
     * @return                     The constructed GetItemRequest.
     */
    public GetItemRequest getUserRequest(String mobileNumber, List<String> projectionExpression){
        String projectionKey = String.join(", ", projectionExpression);
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_NAME, AttributeValue.builder().s(mobileNumber).build());
        return GetItemRequest.builder()
                .tableName(TABLE)
                .key(item)
                .projectionExpression(projectionKey)
                .build();
    }

    public PutItemRequest userCreateRequest(Customer customer){
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(USER_NAME, AttributeValue.builder().s(customer.getMobileNumber()).build());
        item.put("Password", AttributeValue.builder().s(customer.getPassword()).build());
        item.put("CustomerName", AttributeValue.builder().s(customer.getName()).build());
        item.put("Address", AttributeValue.builder().s(customer.getAddress()).build());
        item.put("PINCode", AttributeValue.builder().s(customer.getPinCode()).build());
        item.put("CustomerState", AttributeValue.builder().s(customer.getState()).build());
        item.put("Country", AttributeValue.builder().s(customer.getCountry()).build());
        item.put("SIMType", AttributeValue.builder().s(customer.getSimType()).build());
        item.put("Email", AttributeValue.builder().s(customer.getEmail()).build());
        item.put("UserRole", AttributeValue.builder().s("USER").build());


        return PutItemRequest.builder()
                .tableName(TABLE)
                .conditionExpression("attribute_not_exists(MobileNumber)")
                .item(item)
                .build();
    }
    /**
     * Creates and returns a {@link PutItemRequest} object for adding a new user to the DynamoDB table.
     * This method validates if a user with the provided mobile number already exists before insertion.
     *
     * @param customer A {@link Customer} object containing user information.
     * @return A {@link PutItemRequest} object configured for user creation.
     */
    public UpdateItemRequest userUpdateRequest(Customer customer) {
        Map<String, AttributeValueUpdate> item = new HashMap<>();
        if(!Objects.isNull(customer.getName())) item.put("CustomerName", AttributeValueUpdate.builder().value(AttributeValue.builder().s(customer.getName()).build()).build());
        if(!Objects.isNull(customer.getAddress())) item.put("Address", AttributeValueUpdate.builder().value(AttributeValue.builder().s(customer.getAddress()).build()).build());
        if(!Objects.isNull(customer.getPinCode())) item.put("PINCode", AttributeValueUpdate.builder().value(AttributeValue.builder().s(customer.getPinCode()).build()).build());
        if(!Objects.isNull(customer.getState()))item.put("CustomerState", AttributeValueUpdate.builder().value(AttributeValue.builder().s(customer.getState()).build()).build());
        if(!Objects.isNull(customer.getEmail())) item.put("Email", AttributeValueUpdate.builder().value(AttributeValue.builder().s(customer.getEmail()).build()).build());

        return UpdateItemRequest.builder()
                .tableName(TABLE)
                .key(Collections.singletonMap(USER_NAME, AttributeValue.builder().s(customer.getMobileNumber()).build())) // Assuming MobileNumber is the key
                .attributeUpdates(item)
                .build();
    }
    /**
     * Creates and returns a {@link UpdateItemRequest} object for resetting the password of an existing user.
     * This method updates only the password field based on the provided mobile number and new password.
     *
     * @param mobileNumber The mobile number of the user for password reset.
     * @param newPassword The new password for the user.
     * @return A {@link UpdateItemRequest} object configured for password reset.
     */
    public UpdateItemRequest passwordResetRequest(String mobileNumber, String newPassword){
        Map<String, AttributeValueUpdate> item = new HashMap<>();
        if(!Objects.isNull(newPassword)) item.put("Password", AttributeValueUpdate.builder().value(AttributeValue.builder().s(newPassword).build()).build());

        return UpdateItemRequest.builder()
                .tableName(TABLE)
                .key(Collections.singletonMap(USER_NAME, AttributeValue.builder().s(mobileNumber).build())) // Assuming MobileNumber is the key
                .attributeUpdates(item)
                .build();
    }
}