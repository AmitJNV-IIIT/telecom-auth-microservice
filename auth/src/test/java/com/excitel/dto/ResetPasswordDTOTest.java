package com.excitel.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ResetPasswordDTOTest {

    @Test
    void testGetterSetter() {
        // Given
        ResetPasswordDTO dto = new ResetPasswordDTO();

        // Set values using setters
        dto.setOldPassword("oldPass");
        dto.setNewPassword("newPass");

        // Verify using getters
        assertEquals("oldPass", dto.getOldPassword());
        assertEquals("newPass", dto.getNewPassword());
    }
    @Test
    void testNoArgsConstructor() {
        // Verify no-args constructor
        ResetPasswordDTO dto = new ResetPasswordDTO();
        assertNotNull(dto);
        assertEquals(null, dto.getOldPassword());
        assertEquals(null, dto.getNewPassword());
    }
}
