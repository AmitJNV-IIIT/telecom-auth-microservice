package com.excitel.exception.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceExceptionTest {
    @Test
    public void testUserServiceExceptionMessage() {
        // Given
        String errorMessage = "User not found";

        // When
        UserServiceException exception = new UserServiceException(errorMessage);

        // Then
        assertEquals(errorMessage, exception.getMessage());
    }
}
