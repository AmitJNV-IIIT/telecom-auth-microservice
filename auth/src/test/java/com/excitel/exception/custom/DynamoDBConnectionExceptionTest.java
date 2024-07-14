package com.excitel.exception.custom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DynamoDBConnectionExceptionTest {

    @Test
    void testExceptionThrown() {
        // Given
        String errorMessage = "DynamoDB connection failed";

        // When & Then
        assertThrows(DynamoDBConnectionException.class, () -> {
            throw new DynamoDBConnectionException(errorMessage);
        });
    }
}
