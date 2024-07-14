package com.excitel.service;

import javax.management.ServiceNotFoundException;

public interface OTPService {
    public String generateOTP();
    public void storeOTP(String mobileNumber, String otp) throws ServiceNotFoundException;
    public String storeOTPWithClientID(String mobileNumber, String clientID, String hashValue) throws ServiceNotFoundException;
    public String verifyOTP(String email, String otp) throws ServiceNotFoundException;


}
