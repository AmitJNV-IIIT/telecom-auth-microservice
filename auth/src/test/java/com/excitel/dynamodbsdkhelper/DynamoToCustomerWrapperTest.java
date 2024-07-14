package com.excitel.dynamodbsdkhelper;
import com.excitel.model.Customer;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DynamoToCustomerWrapperTest {

    private final DynamoToCustomerWrapper wrapper = new DynamoToCustomerWrapper();

    @Test
    void mapToCustomer_withAllAttributes() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("MobileNumber", AttributeValue.builder().s("1234567890").build());
        item.put("Password", AttributeValue.builder().s("password123").build());
        item.put("CustomerName", AttributeValue.builder().s("John Doe").build());
        item.put("Address", AttributeValue.builder().s("123 Main St").build());
        item.put("PINCode", AttributeValue.builder().s("12345").build());
        item.put("CustomerState", AttributeValue.builder().s("State").build());
        item.put("Country", AttributeValue.builder().s("Country").build());
        item.put("SIMType", AttributeValue.builder().s("Type").build());
        item.put("Email", AttributeValue.builder().s("test@example.com").build());
        item.put("UserRole", AttributeValue.builder().s("USER").build());

        Customer customer = wrapper.mapToCustomer(item);

        assertNotNull(customer);
        assertEquals("1234567890", customer.getMobileNumber());
        assertEquals("password123", customer.getPassword());
        assertEquals("John Doe", customer.getName());
        assertEquals("123 Main St", customer.getAddress());
        assertEquals("12345", customer.getPinCode());
        assertEquals("State", customer.getState());
        assertEquals("Country", customer.getCountry());
        assertEquals("Type", customer.getSimType());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals("USER", customer.getRole());
    }

    @Test
    void mapToCustomer_withMissingAttributes() {
        Map<String, AttributeValue> item = new HashMap<>();
        // Add only MobileNumber and Email attributes

        item.put("MobileNumber", AttributeValue.builder().s("1234567890").build());
        item.put("Email", AttributeValue.builder().s("test@example.com").build());

        Customer customer = wrapper.mapToCustomer(item);

        assertNotNull(customer);
        assertEquals("1234567890", customer.getMobileNumber());
        assertEquals(null, customer.getPassword());
        assertEquals(null, customer.getName());
        assertEquals(null, customer.getAddress());
        assertEquals(null, customer.getPinCode());
        assertEquals(null, customer.getState());
        assertEquals(null, customer.getCountry());
        assertEquals(null, customer.getSimType());
        assertEquals("test@example.com", customer.getEmail());
        assertEquals(null, customer.getRole());
    }

    @Test
    void mapToCustomer_withEmptyAttributes() {
        Map<String, AttributeValue> item = new HashMap<>();
        // Add empty attributes

        item.put("MobileNumber", AttributeValue.builder().s("").build());
        item.put("Email", AttributeValue.builder().s("").build());

        Customer customer = wrapper.mapToCustomer(item);

        assertNotNull(customer);
        assertEquals("", customer.getMobileNumber());
        assertEquals(null, customer.getPassword());
        assertEquals(null, customer.getName());
        assertEquals(null, customer.getAddress());
        assertEquals(null, customer.getPinCode());
        assertEquals(null, customer.getState());
        assertEquals(null, customer.getCountry());
        assertEquals(null, customer.getSimType());
        assertEquals("", customer.getEmail());
        assertEquals(null, customer.getRole());
    }
//    @Test
//    void mapToCustomer_withNullItem() {
//        Map<String, AttributeValue> item = null;
//
//        Customer customer = wrapper.mapToCustomer(item);
//
//        assertNull(customer);
//    }

    @Test
    void mapToCustomer_withEmptyItem() {
        Map<String, AttributeValue> item = new HashMap<>();

        Customer customer = wrapper.mapToCustomer(item);

        assertNotNull(customer);
        assertNull(customer.getMobileNumber());
        assertNull(customer.getPassword());
        assertNull(customer.getName());
        assertNull(customer.getAddress());
        assertNull(customer.getPinCode());
        assertNull(customer.getState());
        assertNull(customer.getCountry());
        assertNull(customer.getSimType());
        assertNull(customer.getEmail());
        assertNull(customer.getRole());
    }

    @Test
    void mapToCustomer_withMissingIndividualAttributes() {
        Map<String, AttributeValue> item = new HashMap<>();
        // Add only some attributes

        item.put("MobileNumber", AttributeValue.builder().s("1234567890").build());
        item.put("CustomerName", AttributeValue.builder().s("John Doe").build());
        item.put("Email", AttributeValue.builder().s("test@example.com").build());

        Customer customer = wrapper.mapToCustomer(item);

        assertNotNull(customer);
        assertEquals("1234567890", customer.getMobileNumber());
        assertNull(customer.getPassword());
        assertEquals("John Doe", customer.getName());
        assertNull(customer.getAddress());
        assertNull(customer.getPinCode());
        assertNull(customer.getState());
        assertNull(customer.getCountry());
        assertNull(customer.getSimType());
        assertEquals("test@example.com", customer.getEmail());
        assertNull(customer.getRole());
    }

    @Test
    void mapToCustomer_withEmptyStringAttributes() {
        Map<String, AttributeValue> item = new HashMap<>();
        // Add empty string attributes

        item.put("MobileNumber", AttributeValue.builder().s("").build());
        item.put("Password", AttributeValue.builder().s("").build());
        item.put("CustomerName", AttributeValue.builder().s("").build());
        item.put("Address", AttributeValue.builder().s("").build());
        item.put("PINCode", AttributeValue.builder().s("").build());
        item.put("CustomerState", AttributeValue.builder().s("").build());
        item.put("Country", AttributeValue.builder().s("").build());
        item.put("SIMType", AttributeValue.builder().s("").build());
        item.put("Email", AttributeValue.builder().s("").build());
        item.put("UserRole", AttributeValue.builder().s("").build());

        Customer customer = wrapper.mapToCustomer(item);

        assertNotNull(customer);
        assertEquals("", customer.getMobileNumber());
        assertEquals("", customer.getPassword());
        assertEquals("", customer.getName());
        assertEquals("", customer.getAddress());
        assertEquals("", customer.getPinCode());
        assertEquals("", customer.getState());
        assertEquals("", customer.getCountry());
        assertEquals("", customer.getSimType());
        assertEquals("", customer.getEmail());
        assertEquals("", customer.getRole());
    }

//    @Test
//    void mapToCustomer_withNullAttributeValues() {
//        Map<String, AttributeValue> item = new HashMap<>();
//        // Add null attribute values
//
//        item.put("MobileNumber", null);
//        item.put("Password", null);
//        item.put("CustomerName", null);
//        item.put("Address", null);
//        item.put("PINCode", null);
//        item.put("CustomerState", null);
//        item.put("Country", null);
//        item.put("SIMType", null);
//        item.put("Email", null);
//        item.put("UserRole", null);
//
//        Customer customer = wrapper.mapToCustomer(item);
//
//        assertNotNull(customer);
//        assertNull(customer.getMobileNumber());
//        assertNull(customer.getPassword());
//        assertNull(customer.getName());
//        assertNull(customer.getAddress());
//        assertNull(customer.getPinCode());
//        assertNull(customer.getState());
//        assertNull(customer.getCountry());
//        assertNull(customer.getSimType());
//        assertNull(customer.getEmail());
//        assertNull(customer.getRole());
//    }


    // Additional test cases can be added to cover more scenarios
}