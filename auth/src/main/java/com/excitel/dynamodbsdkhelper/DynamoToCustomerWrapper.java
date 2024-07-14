package com.excitel.dynamodbsdkhelper;

import com.excitel.model.Customer;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
@Service
public class DynamoToCustomerWrapper {
    /**
     * Maps a DynamoDB item to a Customer object.
     *
     * @param item The DynamoDB item to map.
     * @return     The mapped Customer object.
     */
    public Customer mapToCustomer(Map<String, AttributeValue> item) {
        Customer customer = new Customer();

        customer.setMobileNumber(item.getOrDefault("MobileNumber", AttributeValue.builder().s(null).build()).s());
        customer.setPassword(item.getOrDefault("Password", AttributeValue.builder().s(null).build()).s());
        customer.setName(item.getOrDefault("CustomerName", AttributeValue.builder().s(null).build()).s());
        customer.setAddress(item.getOrDefault("Address", AttributeValue.builder().s(null).build()).s());
        customer.setPinCode(item.getOrDefault("PINCode", AttributeValue.builder().s(null).build()).s());
        customer.setState(item.getOrDefault("CustomerState", AttributeValue.builder().s(null).build()).s());
        customer.setCountry(item.getOrDefault("Country", AttributeValue.builder().s(null).build()).s());
        customer.setSimType(item.getOrDefault("SIMType", AttributeValue.builder().s(null).build()).s());
        customer.setEmail(item.getOrDefault("Email", AttributeValue.builder().s(null).build()).s());
        customer.setRole(item.getOrDefault("UserRole", AttributeValue.builder().s(null).build()).s());

        return customer;
    }
}
