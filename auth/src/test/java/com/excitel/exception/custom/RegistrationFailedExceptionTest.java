package com.excitel.exception.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationFailedExceptionTest {

    @Test
    void testExceptionWithCause() {
        // Given
        String errorMessage = "Registration failed";
        Throwable cause = new RuntimeException("Internal error");

        // When
        RegistrationFailedException exception = new RegistrationFailedException(errorMessage, cause);

        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testExceptionWithoutCause() {
        // Given
        String errorMessage = "Registration failed";

        // When
        RegistrationFailedException exception = new RegistrationFailedException(errorMessage);

        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
}
