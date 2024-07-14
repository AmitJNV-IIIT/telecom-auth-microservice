package com.excitel.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JWTRequestDTOTest {

    @Test
    void testNoArgsConstructor() {
        // When
        JwtRequestDTO jwtRequestDTO = new JwtRequestDTO();

        // Then
        assertNotNull(jwtRequestDTO);
        assertEquals(null, jwtRequestDTO.getMobileNumber());
        assertEquals(null, jwtRequestDTO.getPassword());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        String mobileNumber = "1234567890";
        String password = "securepassword";

        // When
        JwtRequestDTO jwtRequestDTO = new JwtRequestDTO(mobileNumber, password);

        // Then
        assertNotNull(jwtRequestDTO);
        assertEquals(mobileNumber, jwtRequestDTO.getMobileNumber());
        assertEquals(password, jwtRequestDTO.getPassword());
    }

    @Test
    void testGettersAndSetters() {
        // Given
        String mobileNumber = "1234567890";
        String password = "securepassword";

        // When
        JwtRequestDTO jwtRequestDTO = new JwtRequestDTO();
        jwtRequestDTO.setMobileNumber(mobileNumber);
        jwtRequestDTO.setPassword(password);

        // Then
        assertEquals(mobileNumber, jwtRequestDTO.getMobileNumber());
        assertEquals(password, jwtRequestDTO.getPassword());
    }

//    @Test
//    void testToString() {
//        // Given
//        String mobileNumber = "1234567890";
//        String password = "securepassword";
//
//        // When
//        JwtRequestDTO jwtRequestDTO = new JwtRequestDTO(mobileNumber, password);
//
//        // Then
//        String expectedToString = "JwtRequestDTO(mobileNumber=1234567890, password=securepassword)";
//        assertEquals(expectedToString, jwtRequestDTO.toString());
//    }
}
