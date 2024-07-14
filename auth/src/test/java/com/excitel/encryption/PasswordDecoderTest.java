package com.excitel.encryption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordDecoderTest {

    private PasswordDecoder passwordDecoder;
    private final String secretKey = "mySecretKey";

    @BeforeEach
    void setUp() {
        passwordDecoder = new PasswordDecoder();
        ReflectionTestUtils.setField(passwordDecoder, "secretKey", secretKey);
    }

    @Test
    void testDecodePassword_InvalidInput() {
        String encryptedPassword = Base64.getEncoder().encodeToString("short".getBytes());
        assertThrows(NullPointerException.class, () -> passwordDecoder.decodePassword(encryptedPassword));
    }

    @Test
    void testDecodePassword_ValidInput() {
        String originalPassword = "myPassword";
        String encryptedPassword = Base64.getEncoder().encodeToString((originalPassword + secretKey).getBytes());

        String decodedPassword = passwordDecoder.decodePassword(encryptedPassword);

        assertEquals(originalPassword, decodedPassword);
    }

    @Test
    void testDecodePassword_EmptyInput() {
        String encryptedPassword = "";
        assertThrows(NullPointerException.class, () -> passwordDecoder.decodePassword(encryptedPassword));
    }

    @Test
    void testDecodePassword_NullInput() {
        assertThrows(NullPointerException.class, () -> passwordDecoder.decodePassword(null));
    }
}