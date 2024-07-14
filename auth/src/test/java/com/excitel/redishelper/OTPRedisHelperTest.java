package com.excitel.redishelper;

import com.excitel.model.OtpDB;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class OTPRedisHelperTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private OTPRedisHelper otpRedisHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSaveOTP() throws Exception {
        // Mock data
        String mobileNumber = "1234567890";
        OtpDB otpDB = new OtpDB();

        // Mock behavior
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(otpDB)).thenReturn("otpDbJson");

        // Call the method
        otpRedisHelper.saveOTP(mobileNumber, otpDB);

        // Verify method calls
        verify(valueOperations, times(1)).set(anyString(), anyString(), eq(15L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testGetOtp() throws Exception {
        // Mock data
        String mobileNumber = "1234567890";
        OtpDB otpDB = new OtpDB();

        // Mock behavior
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn("otpDbJson");
        when(objectMapper.readValue("otpDbJson", OtpDB.class)).thenReturn(otpDB);

        // Call the method
        OtpDB result = otpRedisHelper.getOtp(mobileNumber);

        // Verify method calls and result
        verify(valueOperations, times(1)).get(anyString());
        verify(objectMapper, times(1)).readValue("otpDbJson", OtpDB.class);
        assertEquals(otpDB, result);
    }

    @Test
    void testSaveHashAndKey() throws Exception {
        // Mock data
        String mobileNumber = "1234567890";
        OtpDB otpDB = new OtpDB();

        // Mock behavior
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(objectMapper.writeValueAsString(otpDB)).thenReturn("otpDbJson");

        // Call the method
        otpRedisHelper.saveHashAndKey(mobileNumber, otpDB);

        // Verify method calls
        verify(valueOperations, times(1)).set(anyString(), anyString(), eq(5L), eq(TimeUnit.MINUTES));
    }

    @Test
    void testRemoveOTP() {
        // Mock data
        String mobileNumber = "1234567890";

        // Call the method
        otpRedisHelper.removeOTP(mobileNumber);

        // Verify method call
        verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    public void test_saveOTP_throws_IOException_on_objectMapper_JsonProcessingException() throws IOException {
        OtpDB otpDB = new OtpDB();
        String mobileNumber = "1234567890";
        String expectedErrorMessage = "Error converting OtpDB to Json";

        // Mock behavior to throw JsonProcessingException
        JsonProcessingException jsonProcessingException = new JsonProcessingException(expectedErrorMessage) {};
        doThrow(jsonProcessingException).when(objectMapper).writeValueAsString(otpDB);

        // Call the method and expect IOException
        try {
            otpRedisHelper.saveOTP(mobileNumber, otpDB);
            fail("Expected IOException with message: " + expectedErrorMessage);
        } catch (IOException e) {
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    @Test
    public void test_getOtp_throws_IOException_on_objectMapper_readValue_exception() throws IOException {
        String mobileNumber = "1234567890";
        String otpDbJson = "{\"<message>\":\"Converted to Json\"}";

        // Mock ValueOperations
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        // Mock behavior
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(otpDbJson);

        // Stub ObjectMapper behavior to throw IOException
        IOException ioException = new IOException("Error converting Json to OtpDB");
        doAnswer(invocation -> {
            throw ioException;
        }).when(objectMapper).readValue(anyString(), eq(OtpDB.class));

        // Call the method and expect IOException
        try {
            otpRedisHelper.getOtp(mobileNumber);
            fail("Expected IOException on ObjectMapper.readValue");
        } catch (IOException e) {
            assertEquals(ioException.getMessage(), e.getMessage());
        }
    }


    @Test
    public void test_saveHashAndKey_catches_IOException() throws IOException {
        // Mock data
        String mobileNumber = "1234567890";
        OtpDB otpDB = new OtpDB();

        // Mock behavior of objectMapper.writeValueAsString to throw JsonProcessingException
        doThrow(JsonProcessingException.class).when(objectMapper).writeValueAsString(otpDB);

        // Call the method and expect IOException to be caught
        assertThrows(IOException.class, () -> otpRedisHelper.saveHashAndKey(mobileNumber, otpDB));
    }



}