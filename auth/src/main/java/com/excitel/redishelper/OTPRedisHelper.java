package com.excitel.redishelper;
import com.excitel.model.OtpDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
/**
 * This class provides helper methods for storing and retrieving OTP (One-Time Password) data using Redis.
 * It uses a {@link StringRedisTemplate} to interact with Redis and a {@link ObjectMapper} for JSON serialization/deserialization.
 *
 * @author (your name here) (if applicable)
 * @since (version of your application) (if applicable)
 */
@Component
public class OTPRedisHelper {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
     private final Logger log = LoggerFactory.getLogger(OTPRedisHelper.class);

    public OTPRedisHelper(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }
    /**
     * Saves the provided {@link OtpDB} object containing OTP data for the given mobile number in Redis.
     * This method serializes the object to JSON before storing it with a key formed by
     * the mobile number and "_otp" suffix. The OTP is set to expire after 15 minutes.
     *
     * @param mobileNumber The mobile number for which to save OTP data.
     * @param otpDB An {@link OtpDB} object containing OTP details.
     * @throws IOException If an error occurs during JSON serialization.
     */
    public void saveOTP(String mobileNumber, OtpDB otpDB) throws IOException {
        try {
            log.info("Redis set method");
            String key = mobileNumber + "_otp";
            String otpDbJson = objectMapper.writeValueAsString(otpDB);
            redisTemplate.opsForValue().set(key, otpDbJson, 15, TimeUnit.MINUTES);
        }catch (IOException exception){
            throw new IOException(exception.getMessage());
        }
    }
    /**
     * Retrieves the {@link OtpDB} object containing OTP data for the given mobile number from Redis.
     * This method retrieves the JSON-serialized data using the key formed by the mobile number and
     * "_otp" suffix, then deserializes it to an `OtpDB` object.
     *
     * @param mobileNumber The mobile number for which to retrieve OTP data.
     * @return An {@link OtpDB} object containing retrieved OTP data or null if not found.
     * @throws IOException If an error occurs during JSON deserialization or data retrieval.
     */
    public OtpDB getOtp(String mobileNumber) throws IOException {
        try {
            log.info("Redis get method");
            String key = mobileNumber + "_otp";
            String otpDbJson = redisTemplate.opsForValue().get(key);
            return objectMapper.readValue(otpDbJson, OtpDB.class);
        } catch (IOException exception) {
            throw new IOException(exception.getMessage());
        }
    }

    /**
     * Saves the provided {@link OtpDB} object as JSON data with a specific key in Redis.
     * This method is for potential use cases where a different key structure or expiration
     * is required compared to the `saveOTP` method.
     *
     * @param mobileNumber The mobile number associated with the OTP data.
     * @param otpDB An {@link OtpDB} object containing OTP details.
     * @throws IOException If an error occurs during JSON serialization.
     */
    public void saveHashAndKey(String mobileNumber, OtpDB otpDB) throws IOException {
        try {
            String key = mobileNumber + "_hash_key";
            String otpDbJson = objectMapper.writeValueAsString(otpDB);
            redisTemplate.opsForValue().set(key, otpDbJson, 5, TimeUnit.MINUTES);
        }catch(IOException exception){
            throw new IOException(exception.getMessage());
        }
    }
    /**
     * Removes the OTP data for the given mobile number from Redis.
     * This method uses a key formed by the mobile number and "_otp" suffix for deletion.
     *
     * @param mobileNumber The mobile number for which to remove OTP data.
     */
    public void removeOTP(String mobileNumber){
        String key = mobileNumber + "_otp";
        redisTemplate.delete(key);
    }
}