package com.excitel.dynamodbsdkhelper;
import com.excitel.model.Customer;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.excitel.dynamodbsdkhelper.AuthQueryBuilder.TABLE;
import static com.excitel.dynamodbsdkhelper.AuthQueryBuilder.USER_NAME;
import static org.junit.jupiter.api.Assertions.*;

class AuthQueryBuilderTest {

    private final AuthQueryBuilder authQueryBuilder = new AuthQueryBuilder();

    @Test
    void mobileQueryRequest_withValidInput() {
        String mobileNumber = "1234567890";
        String tableName = "test-table";
        List<String> projectionList = Arrays.asList("Attribute1", "Attribute2");

        QueryRequest queryRequest = authQueryBuilder.mobileQqueryRequest(mobileNumber, tableName, projectionList);

        assertNotNull(queryRequest);
        assertEquals(projectionList, Arrays.asList(queryRequest.projectionExpression().split(",\\s*")));
        assertEquals(tableName, queryRequest.tableName());
        assertEquals("MobileNumber = :v", queryRequest.keyConditionExpression());
    }

    @Test
    void mobileQueryRequest_withEmptyProjectionList() {
        String mobileNumber = "1234567890";
        String tableName = "test-table";
        List<String> emptyProjectionList = Collections.emptyList();

        QueryRequest queryRequest = authQueryBuilder.mobileQqueryRequest(mobileNumber, tableName, emptyProjectionList);

        assertNotNull(queryRequest);
        assertEquals("", queryRequest.projectionExpression());
    }

    @Test
    void emailQueryRequest_withValidInput() {
        String email = "test@example.com";
        String tableName = "test-table";
        List<String> projectionList = Arrays.asList("Attribute1", "Attribute2");

        QueryRequest queryRequest = authQueryBuilder.emailQueryRequest(email, tableName, projectionList);

        assertNotNull(queryRequest);
        assertEquals(projectionList, Arrays.asList(queryRequest.projectionExpression().split(",\\s*")));
        assertEquals(tableName, queryRequest.tableName());
        assertEquals("Email = :v", queryRequest.keyConditionExpression());
        assertEquals("Email-index", queryRequest.indexName());
    }

    @Test
    void emailQueryRequest_withEmptyProjectionList() {
        String email = "test@example.com";
        String tableName = "test-table";
        List<String> emptyProjectionList = Collections.emptyList();

        QueryRequest queryRequest = authQueryBuilder.emailQueryRequest(email, tableName, emptyProjectionList);

        assertNotNull(queryRequest);
        assertEquals("", queryRequest.projectionExpression());
    }

    @Test
    void getUserRequest_withValidInput() {
        String mobileNumber = "1234567890";
        List<String> projectionExpression = Arrays.asList("Attribute1", "Attribute2");

        GetItemRequest getItemRequest = authQueryBuilder.getUserRequest(mobileNumber, projectionExpression);

        assertNotNull(getItemRequest);
        assertEquals(projectionExpression, Arrays.asList(getItemRequest.projectionExpression().split(",\\s*")));
        assertEquals("customer-table", getItemRequest.tableName());
        assertNotNull(getItemRequest.key());
        assertEquals(1, getItemRequest.key().size());
        assertEquals("1234567890", getItemRequest.key().get("MobileNumber").s());
    }
    @Test
    void userCreateRequest_withValidInput() {
        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");
        customer.setPassword("password123");
        customer.setName("John Doe");
        customer.setAddress("123 Main St");
        customer.setPinCode("12345");
        customer.setState("State");
        customer.setCountry("Country");
        customer.setSimType("Type");
        customer.setEmail("test@example.com");

        PutItemRequest putItemRequest = authQueryBuilder.userCreateRequest(customer);

        assertNotNull(putItemRequest);
        assertEquals("customer-table", putItemRequest.tableName());
        assertEquals("attribute_not_exists(MobileNumber)", putItemRequest.conditionExpression());
        assertNotNull(putItemRequest.item());
        assertEquals(10, putItemRequest.item().size());
        assertEquals("1234567890", putItemRequest.item().get("MobileNumber").s());
        assertEquals("password123", putItemRequest.item().get("Password").s());
        assertEquals("John Doe", putItemRequest.item().get("CustomerName").s());
        assertEquals("123 Main St", putItemRequest.item().get("Address").s());
        assertEquals("12345", putItemRequest.item().get("PINCode").s());
        assertEquals("State", putItemRequest.item().get("CustomerState").s());
        assertEquals("Country", putItemRequest.item().get("Country").s());
        assertEquals("Type", putItemRequest.item().get("SIMType").s());
        assertEquals("test@example.com", putItemRequest.item().get("Email").s());
        assertEquals("USER", putItemRequest.item().get("UserRole").s());
    }

    @Test
    void passwordResetRequest_withValidInput() {
        String mobileNumber = "1234567890";
        String newPassword = "newPassword123";

        UpdateItemRequest updateItemRequest = authQueryBuilder.passwordResetRequest(mobileNumber, newPassword);

        assertNotNull(updateItemRequest);
        assertEquals("customer-table", updateItemRequest.tableName());
        assertNotNull(updateItemRequest.key());
        assertEquals(1, updateItemRequest.key().size());
        assertEquals("1234567890", updateItemRequest.key().get("MobileNumber").s());
        assertNotNull(updateItemRequest.attributeUpdates());
        assertEquals(1, updateItemRequest.attributeUpdates().size());
        assertEquals("newPassword123", updateItemRequest.attributeUpdates().get("Password").value().s());
    }

    @Test
    public void testUserUpdateRequest_FullCustomerData() {
        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");
        customer.setName("John Doe");
        customer.setAddress("123 Main St");
        customer.setPinCode("12345");
        customer.setState("CA");
        customer.setEmail("john@gmail.com");
        UpdateItemRequest request = authQueryBuilder.userUpdateRequest(customer);
        // Assert on key and attribute updates
        assertEquals(TABLE, request.tableName());
        assertEquals(1, request.key().size());
        assertEquals(customer.getMobileNumber(), request.key().get(USER_NAME).s());
        assertEquals(5, request.attributeUpdates().size());  // All except Name present

    }

    @Test
    public void testUserUpdateRequest_NoName() {

        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");
        customer.setName(null);
        customer.setAddress("123 Main St");
        customer.setPinCode("12345");
        customer.setState("CA");
        customer.setEmail("john@gmail.com");
        UpdateItemRequest request = authQueryBuilder.userUpdateRequest(customer);

        // Assert on key and attribute updates
        assertEquals(TABLE, request.tableName());
        assertEquals(1, request.key().size());
        assertEquals(customer.getMobileNumber(), request.key().get(USER_NAME).s());
        assertEquals(4, request.attributeUpdates().size());  // All except Name present

        // Optional: Assert absence of specific attribute
        assertFalse(request.attributeUpdates().containsKey("CustomerName"));
    }

    @Test
    public void testUserUpdateRequest_NoAddress() {
        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");
        customer.setName("John Doe");
        customer.setAddress(null);
        customer.setPinCode("12345");
        customer.setState("CA");
        customer.setEmail("john@gmail.com");
        UpdateItemRequest request = authQueryBuilder.userUpdateRequest(customer);

        // Assert on key and attribute updates
        assertEquals(TABLE, request.tableName());
        assertEquals(1, request.key().size());
        assertEquals(customer.getMobileNumber(), request.key().get(USER_NAME).s());
        assertEquals(4, request.attributeUpdates().size());  // All except Name present

        // Optional: Assert absence of specific attribute
        assertFalse(request.attributeUpdates().containsKey("Address"));
    }

    @Test
    public void testUserUpdateRequest_NoPinCode() {
        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");
        customer.setName("John Doe");
        customer.setAddress("123 Street");
        customer.setPinCode(null);
        customer.setState("CA");
        customer.setEmail("john@gmail.com");
        UpdateItemRequest request = authQueryBuilder.userUpdateRequest(customer);

        // Assert on key and attribute updates
        assertEquals(TABLE, request.tableName());
        assertEquals(1, request.key().size());
        assertEquals(customer.getMobileNumber(), request.key().get(USER_NAME).s());
        assertEquals(4, request.attributeUpdates().size());  // All except Name present

        // Optional: Assert absence of specific attribute
        assertFalse(request.attributeUpdates().containsKey("PINCode"));
    }

    @Test
    public void testUserUpdateRequest_NoState() {
        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");
        customer.setName("John Doe");
        customer.setAddress("123 Street");
        customer.setPinCode("12345");
        customer.setState(null);
        customer.setEmail("john@gmail.com");
        UpdateItemRequest request = authQueryBuilder.userUpdateRequest(customer);

        // Assert on key and attribute updates
        assertEquals(TABLE, request.tableName());
        assertEquals(1, request.key().size());
        assertEquals(customer.getMobileNumber(), request.key().get(USER_NAME).s());
        assertEquals(4, request.attributeUpdates().size());  // All except Name present

        // Optional: Assert absence of specific attribute
        assertFalse(request.attributeUpdates().containsKey("CustomerState"));
    }


    @Test
    public void testUserUpdateRequest_NoEmail() {
        Customer customer = new Customer();
        customer.setMobileNumber("1234567890");
        customer.setName("John Doe");
        customer.setAddress("123 Street");
        customer.setPinCode("12345");
        customer.setState("CA");
        customer.setEmail(null);
        UpdateItemRequest request = authQueryBuilder.userUpdateRequest(customer);

        // Assert on key and attribute updates
        assertEquals(TABLE, request.tableName());
        assertEquals(1, request.key().size());
        assertEquals(customer.getMobileNumber(), request.key().get(USER_NAME).s());
        assertEquals(4, request.attributeUpdates().size());  // All except Name present

        // Optional: Assert absence of specific attribute
        assertFalse(request.attributeUpdates().containsKey("Email"));
    }
    @Test
    public void testPasswordResetRequest_WithNewPassword() {
        String mobileNumber = "1234567890";
        String newPassword = "securePassword";

        UpdateItemRequest request = authQueryBuilder.passwordResetRequest(mobileNumber, newPassword);

        // Assert on table name, key, and attribute updates
        assertEquals(TABLE, request.tableName());
        assertEquals(1, request.key().size());
        assertEquals(mobileNumber, request.key().get(USER_NAME).s());
        assertEquals(1, request.attributeUpdates().size());
        assertEquals(newPassword, request.attributeUpdates().get("Password").value().s());
    }

    @Test
    public void testPasswordResetRequest_WithoutNewPassword() {
        String mobileNumber = "1234567890";

        // We don't provide a new password

        UpdateItemRequest request = authQueryBuilder.passwordResetRequest(mobileNumber, null);

        // Assert on table name, key, and attribute updates
        assertEquals(TABLE, request.tableName());
        assertEquals(1, request.key().size());
        assertEquals(mobileNumber, request.key().get(USER_NAME).s());
        assertEquals(0, request.attributeUpdates().size()); // No attribute updates since password is null
    }
}