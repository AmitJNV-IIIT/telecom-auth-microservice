package com.excitel.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NewPasswordDTOTest {

    @Test
    void testAllArgsConstructor() {
        // Given
        String status = "success";
        String mobileNumber = "1234567890";
        String keyAndHash = "key:hash";
        String newPassword = "newSecurePassword";

        // When
        NewPasswordDTO newPasswordDTO = new NewPasswordDTO(status, mobileNumber, keyAndHash, newPassword);

        // Then
        assertNotNull(newPasswordDTO);
        assertEquals(status, newPasswordDTO.getStatus());
        assertEquals(mobileNumber, newPasswordDTO.getMobileNumber());
        assertEquals(keyAndHash, newPasswordDTO.getKeyAndHash());
        assertEquals(newPassword, newPasswordDTO.getNewPassword());
    }
}
