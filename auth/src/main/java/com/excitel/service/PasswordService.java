package com.excitel.service;

import com.excitel.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import javax.management.ServiceNotFoundException;
import java.io.IOException;

public interface PasswordService {

    ResponseEntity<ResponseDTO> forgotPassword(String mobileNumber);
    ResponseEntity<ResponseDTO>sendOTP(String mobileNumber);
    public ResponseEntity<ResponseDTO> verifyOTP(String mobileNumber, String otp);
    public String generateKeyAndHash(String mobileNumber) throws ServiceNotFoundException;
    public boolean updatePassword(String newPassword, String mobileNumber, String keyAndHash) throws IOException;
}
