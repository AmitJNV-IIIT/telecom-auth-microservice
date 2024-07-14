package com.excitel.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseDTOTest {
    @Test
    public void testResponseDTO_ConstructorAndGetters() {
        // Create a HttpStatus object
        HttpStatus status = HttpStatus.OK;

        // Prepare test data
        String message = "Success";
        String response = "Some response data";

        // Create a ResponseDTO instance using the all-args constructor
        ResponseDTO responseDTO = new ResponseDTO(status, message, response);

        // Verify the values set by the constructor using getters
        assertEquals(status, responseDTO.getStatus());
        assertEquals(message, responseDTO.getMessage());
        assertEquals(response, responseDTO.getResponse());
    }

    @Test
    public void testResponseDTO_Setters() {
        // Create a HttpStatus object
        HttpStatus status = HttpStatus.BAD_REQUEST;

        // Create a new ResponseDTO instance
        ResponseDTO responseDTO = new ResponseDTO();

        // Set values using setter methods
        responseDTO.setStatus(status);
        responseDTO.setMessage("Error");
        responseDTO.setResponse("Bad input");

        // Verify the values set by the setters using getters
        assertEquals(status, responseDTO.getStatus());
        assertEquals("Error", responseDTO.getMessage());
        assertEquals("Bad input", responseDTO.getResponse());
    }
}
