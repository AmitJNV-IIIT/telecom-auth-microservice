package com.excitel.dto;

import com.excitel.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserDTOTest {
    @Test
    void testNoArgsConstructor() {
        // Create an instance using the no-args constructor
        UserDTO userDTO = new UserDTO();

        // Verify that the instance is not null
        assertNotNull(userDTO);
        // Verify that the HttpStatus is null by default (no-args constructor behavior)
        assertEquals(null, userDTO.getStatus());
        // Verify that the message is null by default (no-args constructor behavior)
        assertEquals(null, userDTO.getMessage());
        // Verify that the Customer data is null by default (no-args constructor behavior)
        assertEquals(null, userDTO.getData());
    }

    @Test
    void testAllArgsConstructor() {
        // Given values
        HttpStatus status = HttpStatus.OK;
        String message = "Success";
        Customer data = new Customer();

        // Create an instance using the all-args constructor
        UserDTO userDTO = new UserDTO(status, message, data);

        // Verify that the instance is not null
        assertNotNull(userDTO);
        // Verify that the HttpStatus is set correctly
        assertEquals(status, userDTO.getStatus());
        // Verify that the message is set correctly
        assertEquals(message, userDTO.getMessage());
        // Verify that the Customer data is set correctly
        assertEquals(data, userDTO.getData());
    }

    @Test
    void testGetterAndSetter() {
        // Create an instance
        UserDTO userDTO = new UserDTO();

        // Given values
        HttpStatus status = HttpStatus.OK;
        String message = "Success";
        Customer data = new Customer();

        // Set values using setters
        userDTO.setStatus(status);
        userDTO.setMessage(message);
        userDTO.setData(data);

        // Verify that the values are retrieved correctly using getters
        assertEquals(status, userDTO.getStatus());
        assertEquals(message, userDTO.getMessage());
        assertEquals(data, userDTO.getData());
    }

}
