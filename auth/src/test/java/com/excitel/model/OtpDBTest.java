package com.excitel.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OtpDBTest {
    @Test
    public void testNoArgsConstructor() {
        OtpDB otpDB = new OtpDB();
        assertNotNull(otpDB);
        assertNull(otpDB.getMobileNumber());
        assertNull(otpDB.getOtp());
        assertNull(otpDB.getClientID());
        assertNull(otpDB.getHashKey());
    }

    @Test
    public void testAllArgsConstructor() {
        String mobileNumber = "1234567890";
        String otp = "1234";
        String clientID = "client1";
        String hashKey = "hash123";

        OtpDB otpDB = new OtpDB(mobileNumber, otp, clientID, hashKey);

        assertNotNull(otpDB);
        assertEquals(mobileNumber, otpDB.getMobileNumber());
        assertEquals(otp, otpDB.getOtp());
        assertEquals(clientID, otpDB.getClientID());
        assertEquals(hashKey, otpDB.getHashKey());
    }

    @Test
    public void testToString() {
        String mobileNumber = "1234567890";
        String otp = "1234";
        String clientID = "client1";
        String hashKey = "hash123";

        OtpDB otpDB = new OtpDB(mobileNumber, otp, clientID, hashKey);

        String expectedToString = "OtpDB(mobileNumber=" + mobileNumber + ", otp=" + otp + ", clientID=" + clientID + ", hashKey=" + hashKey + ")";
        assertEquals(expectedToString, otpDB.toString());
    }
    @Test
    public void testBuilder() {
        String mobileNumber = "1234567890";
        String otp = "1234";
        String clientID = "client1";
        String hashKey = "hash123";

        OtpDB otpDB = OtpDB.builder()
                .mobileNumber(mobileNumber)
                .otp(otp)
                .clientID(clientID)
                .hashKey(hashKey)
                .build();

        assertNotNull(otpDB);
        assertEquals(mobileNumber, otpDB.getMobileNumber());
        assertEquals(otp, otpDB.getOtp());
        assertEquals(clientID, otpDB.getClientID());
        assertEquals(hashKey, otpDB.getHashKey());
    }

    @Test
    public void testGetterAndSetter() {
        String mobileNumber = "1234567890";
        String otp = "1234";
        String clientID = "client1";
        String hashKey = "hash123";

        OtpDB otpDB = new OtpDB();
        otpDB.setMobileNumber(mobileNumber);
        otpDB.setOtp(otp);
        otpDB.setClientID(clientID);
        otpDB.setHashKey(hashKey);

        assertEquals(mobileNumber, otpDB.getMobileNumber());
        assertEquals(otp, otpDB.getOtp());
        assertEquals(clientID, otpDB.getClientID());
        assertEquals(hashKey, otpDB.getHashKey());
    }
}
