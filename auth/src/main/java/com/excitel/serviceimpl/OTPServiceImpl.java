package com.excitel.serviceimpl;//NOSONAR

import com.excitel.model.OtpDB;
import com.excitel.redishelper.OTPRedisHelper;
import com.excitel.service.OTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.management.ServiceNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of OTPService for generating, storing, and verifying OTPs.
 */

@Service
public class OTPServiceImpl implements OTPService {

    private final OTPRedisHelper otpRedisHelper;

    public OTPServiceImpl(OTPRedisHelper otpRedisHelper ){
        this.otpRedisHelper = otpRedisHelper;
    }
    private final Logger logger = LoggerFactory.getLogger(OTPServiceImpl.class);

    /**
     * Generates a random OTP.
     *
     * @return The generated OTP.
     */
    public String generateOTP() {
        // Generate random OTP
        return UUID.randomUUID().toString().substring(0, 6);
    }
    /**
     * Stores the OTP along with the mobile number and its expiration time.
     *
     * @param mobileNumber The mobile number for which OTP is generated.
     * @param otp          The OTP to be stored.
     */
    @Override
    public void storeOTP(String mobileNumber, String otp) throws ServiceNotFoundException{
        try{
            OtpDB otpDB = new OtpDB(mobileNumber,otp,"","");
            otpRedisHelper.saveOTP(mobileNumber,otpDB);
            logger.info("OTP Generated!");
        }catch (IOException exception){
            logger.error("Twilio SMS Service is facing issues :{} " , exception.getMessage());
            throw new ServiceNotFoundException(exception.getMessage());
        }
    }
    /**
     * Stores the OTP along with client ID and hash value.
     *
     * @param mobileNumber The mobile number for which OTP is generated.
     * @param clientID     The client ID to be associated with the OTP.
     * @param hashValue    The hash value to be associated with the OTP.
     * @return Information about the stored hash pair.
     */
    public String storeOTPWithClientID(String mobileNumber, String clientID, String hashValue) throws ServiceNotFoundException {
        try {
            OtpDB otpDB = otpRedisHelper.getOtp(mobileNumber);
            otpDB.setClientID(clientID);
            otpDB.setHashKey(hashValue);
            otpRedisHelper.saveOTP(mobileNumber, otpDB);
            return "Hash_pair = " + clientID + ": " + hashValue;
        }catch (IOException exception){
            throw new ServiceNotFoundException(exception.getMessage());
        }
    }

    /**
     * Verifies the OTP for a given mobile number.
     *
     * @param mobileNumber The mobile number for which OTP is generated.
     * @param otp          The OTP to be verified.
     * @return A string indicating the verification status.
     */
    public String verifyOTP(String mobileNumber, String otp) throws ServiceNotFoundException {
        try {
            OtpDB otpDB = otpRedisHelper.getOtp(mobileNumber);
            if (Objects.nonNull(otpDB)) {
                logger.info("inside redis get verify otp block");
                if (Objects.equals(otpDB.getOtp(), otp)) return "verified";
                else return "incorrect otp";
            } else return "otp expired";
        }catch (IOException exception){
            throw new ServiceNotFoundException(exception.getMessage());
        }
    }
}