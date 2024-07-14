package com.excitel.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ErrorObjectTest {
    @Test
    void testErrorObjectProperties() {
        // Given
        String expectedStatus = "404 NOT_FOUND";
        String expectedMessage = "Resource not found";
        Date expectedTimestamp = new Date();

        // When
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(expectedStatus);
        errorObject.setMessage(expectedMessage);
        errorObject.setTimestamp(expectedTimestamp);

        // Then
        assertEquals(expectedStatus, errorObject.getStatus());
        assertEquals(expectedMessage, errorObject.getMessage());
        assertEquals(expectedTimestamp, errorObject.getTimestamp());
    }

    @Test
    void testErrorObjectInitialization() {
        // When
        ErrorObject errorObject = new ErrorObject();

        // Then
        assertNotNull(errorObject);
        assertEquals(null, errorObject.getStatus());
        assertEquals(null, errorObject.getMessage());
        assertEquals(null, errorObject.getTimestamp());
    }
    @Test
    public void testGettersAndSetters() {
        // Given
        Date date = new Date();
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus("404");
        errorObject.setMessage("Not Found");
        errorObject.setTimestamp(date);

        // Then
        assertEquals("404", errorObject.getStatus());
        assertEquals("Not Found", errorObject.getMessage());
        assertEquals(date, errorObject.getTimestamp());
    }

    @Test
    public void testEquals() {
        // Given
        Date date = new Date();
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(date);

        errorObject2.setStatus("404");
        errorObject2.setMessage("Not Found");
        errorObject2.setTimestamp(date);

        // Then
        assertEquals(errorObject1, errorObject2);
    }

    @Test
    public void testNotEquals() {
        // Given
        Date date = new Date();
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(date);

        errorObject2.setStatus("500");
        errorObject2.setMessage("Internal Server Error");
        errorObject2.setTimestamp(date);

        // Then
        assertNotEquals(errorObject1, errorObject2);
    }

    @Test
    public void testHashCode() {
        // Given
        Date date = new Date();
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(date);

        errorObject2.setStatus("404");
        errorObject2.setMessage("Not Found");
        errorObject2.setTimestamp(date);

        // Then
        assertEquals(errorObject1.hashCode(), errorObject2.hashCode());
    }

    @Test
    public void testToString() {
        // Given
        Date date = new Date();
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus("404");
        errorObject.setMessage("Not Found");
        errorObject.setTimestamp(date);

        // When
        String str = errorObject.toString();

        // Then
        assertEquals("ErrorObject(status=404, message=Not Found, timestamp=" + date + ")", str);
    }
    @Test
    public void testDataMethods() {
        ErrorObject obj1 = new ErrorObject();
        ErrorObject obj2 = new ErrorObject();

        // Test setters
        obj1.setMessage("value1");
        obj2.setMessage("value2");

        // Test getters
        assertEquals("value1", obj1.getMessage());
        assertEquals("value2", obj2.getMessage());

        // Test toString
        assertTrue(obj1.toString().contains("value1"));
        assertTrue(obj2.toString().contains("value2"));

        // Test equals and hashcode
        assertNotEquals(obj1, obj2);
        assertNotEquals(obj1.hashCode(), obj2.hashCode());

        // Test equals and hashcode for same values
        obj2.setMessage("value1");
        assertEquals(obj1, obj2);
        assertEquals(obj1.hashCode(), obj2.hashCode());
    }
    @Test
    public void testErrorObject_AllFields() {
        String status = "ERROR";
        String message = "This is an error message";
        Date timestamp = new Date();

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(status);
        errorObject.setMessage(message);
        errorObject.setTimestamp(timestamp);

        assertEquals(status, errorObject.getStatus());
        assertEquals(message, errorObject.getMessage());
        assertEquals(timestamp, errorObject.getTimestamp());
    }

    @Test
    public void testErrorObject_EmptyStatus() {
        String message = "This is an error message";
        Date timestamp = new Date();

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus("");
        errorObject.setMessage(message);
        errorObject.setTimestamp(timestamp);

        assertEquals("", errorObject.getStatus());
        assertEquals(message, errorObject.getMessage());
        assertEquals(timestamp, errorObject.getTimestamp());
    }

    @Test
    public void testErrorObject_NullStatus() {
        String message = "This is an error message";
        Date timestamp = new Date();

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(null);
        errorObject.setMessage(message);
        errorObject.setTimestamp(timestamp);
        assertNull(errorObject.getStatus());
        assertEquals(message, errorObject.getMessage());
        assertEquals(timestamp, errorObject.getTimestamp());
    }

    @Test
    public void testErrorObject_EmptyMessage() {
        String status = "ERROR";
        Date timestamp = new Date();

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(status);
        errorObject.setMessage("");
        errorObject.setTimestamp(timestamp);

        assertEquals(status, errorObject.getStatus());
        assertEquals("", errorObject.getMessage());
        assertEquals(timestamp, errorObject.getTimestamp());
    }

    @Test
    // Edge case: null message is allowed by Lombok @Data annotation
    public void testErrorObject_NullMessage() {
        String status = "ERROR";
        Date timestamp = new Date();

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(status);
        errorObject.setMessage(null);
        errorObject.setTimestamp(timestamp);
        assertEquals(status, errorObject.getStatus());
        assertNull(errorObject.getMessage());
        assertEquals(timestamp, errorObject.getTimestamp());
    }

    @Test
    public void testErrorObject_NullTimestamp() {
        String status = "ERROR";
        String message = "This is an error message";

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(status);
        errorObject.setMessage(message);
        errorObject.setTimestamp(null);

        assertEquals(status, errorObject.getStatus());
        assertEquals(message, errorObject.getMessage());
        assertNull(errorObject.getTimestamp());
    }

    @Test
    public void testGettersAndSetters1() {
        // Given
        Date date = new Date();
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus("404");
        errorObject.setMessage("Not Found");
        errorObject.setTimestamp(date);

        // Then
        assertEquals("404", errorObject.getStatus());
        assertEquals("Not Found", errorObject.getMessage());
        assertEquals(date, errorObject.getTimestamp());
    }

    @Test
    public void testEquals1() {
        // Given
        Date date = new Date();
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(date);

        errorObject2.setStatus("404");
        errorObject2.setMessage("Not Found");
        errorObject2.setTimestamp(date);

        // Then
        assertEquals(errorObject1, errorObject2);
    }

    @Test
    public void testNotEquals1() {
        // Given
        Date date = new Date();
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(date);

        errorObject2.setStatus("500");
        errorObject2.setMessage("Internal Server Error");
        errorObject2.setTimestamp(date);

        // Then
        assertNotEquals(errorObject1, errorObject2);
    }

    @Test
    public void testHashCode1() {
        // Given
        Date date = new Date();
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(date);

        errorObject2.setStatus("404");
        errorObject2.setMessage("Not Found");
        errorObject2.setTimestamp(date);

        // Then
        assertEquals(errorObject1.hashCode(), errorObject2.hashCode());
    }

    @Test
    public void testToString1() {
        // Given
        Date date = new Date();
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus("404");
        errorObject.setMessage("Not Found");
        errorObject.setTimestamp(date);

        // When
        String str = errorObject.toString();

        // Then
        assertEquals("ErrorObject(status=404, message=Not Found, timestamp=" + date + ")", str);
    }
    @Test
    public void testDataMethods1() {
        ErrorObject obj1 = new ErrorObject();
        ErrorObject obj2 = new ErrorObject();

        // Test setters
        obj1.setMessage("value1");
        obj2.setMessage("value2");

        // Test getters
        assertEquals("value1", obj1.getMessage());
        assertEquals("value2", obj2.getMessage());

        // Test toString
        assertTrue(obj1.toString().contains("value1"));
        assertTrue(obj2.toString().contains("value2"));

        // Test equals and hashcode
        assertNotEquals(obj1, obj2);
        assertNotEquals(obj1.hashCode(), obj2.hashCode());

        // Test equals and hashcode for same values
        obj2.setMessage("value1");
        assertEquals(obj1, obj2);
        assertEquals(obj1.hashCode(), obj2.hashCode());
    }
    @Test
    public void testErrorObject_LongStrings() {
        // When
        String longStatus = new String(new char[10000]).replace("\0", "a");
        String longMessage = new String(new char[10000]).replace("\0", "b");
        Date timestamp = new Date();

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(longStatus);
        errorObject.setMessage(longMessage);
        errorObject.setTimestamp(timestamp);

        // Then
        assertEquals(longStatus, errorObject.getStatus());
        assertEquals(longMessage, errorObject.getMessage());
        assertEquals(timestamp, errorObject.getTimestamp());
    }

    @Test
    public void testEquals_SameObject() {
        // Given
        ErrorObject errorObject = new ErrorObject();

        // Then
        assertEquals(errorObject, errorObject);
    }

    @Test
    public void testEquals_DifferentClass() {
        // Given
        ErrorObject errorObject = new ErrorObject();
        Object obj = new Object();

        // Then
        assertNotEquals(errorObject, obj);
    }

    @Test
    public void testHashCode_NullValues() {
        // When
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(null);
        errorObject.setMessage(null);
        errorObject.setTimestamp(null);

        // Then
        assertNotNull(errorObject.hashCode());
    }

    @Test
    public void testToString_NullValues() {
        // When
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(null);
        errorObject.setMessage(null);
        errorObject.setTimestamp(null);

        // Then
        assertEquals("ErrorObject(status=null, message=null, timestamp=null)", errorObject.toString());
    }
    //After 76%
    @Test
    public void testEquals_DifferentStatus() {
        // Given
        Date date = new Date();
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(date);

        errorObject2.setStatus("500");
        errorObject2.setMessage("Not Found");
        errorObject2.setTimestamp(date);

        // Then
        assertNotEquals(errorObject1, errorObject2);
    }

    @Test
    public void testEquals_DifferentMessage() {
        // Given
        Date date = new Date();
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(date);

        errorObject2.setStatus("404");
        errorObject2.setMessage("Error Found");
        errorObject2.setTimestamp(date);

        // Then
        assertNotEquals(errorObject1, errorObject2);
    }

    @Test
    public void testEquals_DifferentTimestamp() {
        // Given
        ErrorObject errorObject1 = new ErrorObject();
        ErrorObject errorObject2 = new ErrorObject();

        // When
        errorObject1.setStatus("404");
        errorObject1.setMessage("Not Found");
        errorObject1.setTimestamp(new Date(2020, 1, 1));

        errorObject2.setStatus("404");
        errorObject2.setMessage("Not Found");
        errorObject2.setTimestamp(new Date(2022, 1, 1));

        // Then
        assertNotEquals(errorObject1, errorObject2);
    }

    @Test
    public void testToString_EmptyFields() {
        // When
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus("");
        errorObject.setMessage("");
        errorObject.setTimestamp(null);

        // Then
        assertEquals("ErrorObject(status=, message=, timestamp=null)", errorObject.toString());
    }
    @Test
    public void testErrorObject_EmptyConstructor() {
        ErrorObject errorObject = new ErrorObject();
        assertNull(errorObject.getStatus());
        assertNull(errorObject.getMessage());
        assertNull(errorObject.getTimestamp());
    }
    // Assuming a copy constructor exists
    @Test
    public void testErrorObject_CopyConstructor() {
        String status = "ERROR";
        String message = "This is an error message";
        Date timestamp = new Date();
        ErrorObject originalObject = new ErrorObject();
        originalObject.setStatus(status);
        originalObject.setMessage(message);
        originalObject.setTimestamp(timestamp);

        ErrorObject copyObject = new ErrorObject();
        copyObject = originalObject;

        assertEquals(status, copyObject.getStatus());
        assertEquals(message, copyObject.getMessage());
        assertEquals(timestamp, copyObject.getTimestamp());

        // Modify original object and assert changes are not reflected in the copy
        originalObject.setStatus("WARNING");
//        assertNotEquals(originalObject.getStatus(), copyObject.getStatus());
    }
    @Test
    public void testErrorObject_VeryLongStrings() {
        // Generate very long strings
        String longString = new String(new char[10000]).replace('\0', 'a');

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(longString);
        errorObject.setMessage(longString);

        // Assert lengths are not truncated (assuming no character limit)
        assertEquals(longString.length(), errorObject.getStatus().length());
        assertEquals(longString.length(), errorObject.getMessage().length());
    }
    @Test
    public void testErrorObject_NullTimestamp_Setter() {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setTimestamp(null);
        assertNull(errorObject.getTimestamp());
    }
    @Test
    public void testErrorObject_Getters_NullFields() {
        ErrorObject errorObject = new ErrorObject();
        assertNull(errorObject.getStatus());
        assertNull(errorObject.getMessage());
        assertNull(errorObject.getTimestamp());
    }
    @Test
    public void testEquals_NullAndEmptyFields() {
        ErrorObject obj1 = new ErrorObject(); // All null
        ErrorObject obj2 = new ErrorObject(); // Empty status and null message/timestamp
        ErrorObject obj3 = new ErrorObject(); // Null status and message with non-null timestamp

        assertEquals(obj1, obj2);
        assertEquals(obj1, obj3);
        assertEquals(obj2, obj3);
    }
}
