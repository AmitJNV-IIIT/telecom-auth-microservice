package com.excitel.controller;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.excitel.exception.custom.InvalidRequestBodyException;
import com.excitel.model.Customer;
import com.excitel.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

 class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private Customer mockCustomer;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.openMocks(this);
        mockCustomer = createMockCustomer();
    }

    @Test
    void registerUser_ValidCustomer_Success() {
        // Arrange
        when(customerService.registerCustomer(mockCustomer)).thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<?> response = customerController.registerUser(mockCustomer);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void registerUser_InvalidCustomer_ThrowsInvalidRequestBodyException() {
        // Arrange
        Customer invalidCustomer = new Customer(); // Empty customer object

        // Act & Assert
        assertThrows(InvalidRequestBodyException.class, () -> customerController.registerUser(invalidCustomer));
    }

    private Customer createMockCustomer() {
        Customer customer = new Customer();
        customer.setPassword("password");
        customer.setEmail("test@example.com");
        customer.setMobileNumber("1234567890");
        customer.setName("John Doe");
        customer.setAddress("123 Main St");
        customer.setSimType("SIM");
        customer.setPinCode("12345");
        customer.setCountry("USA");
        customer.setState("CA");
        return customer;
    }
    @Test
     void testNullString() {
        String str = null;
        assertTrue(customerController.isNullOrEmpty(str));
    }

    @Test
      void testEmptyString() {
        String str = "";
        assertTrue(customerController.isNullOrEmpty(str));
    }

    @Test
     void testNonEmptyString() {
        String str = "Hello World";
        assertFalse(customerController.isNullOrEmpty(str));
    }

    @Test
     void testStringWithWhitespace() {
        String str = "  "; // Only whitespace characters
        assertFalse(customerController.isNullOrEmpty(str));
    }
}
