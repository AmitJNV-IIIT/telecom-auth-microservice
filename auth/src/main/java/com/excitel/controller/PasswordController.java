package com.excitel.controller;

import com.excitel.dto.ResponseDTO;
import com.excitel.dto.NewPasswordDTO;
import com.excitel.service.PasswordService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

/**
 * Controller class for handling password-related endpoints.
 */
@RestController
@RequestMapping("/api/v2/auth/password")
public class PasswordController {

    private static final Logger log = LoggerFactory.getLogger(PasswordController.class);
    @Autowired//NOSONAR
    private PasswordService passwordService;
    /**
     * Endpoint for initiating the password reset process by sending an OTP to the provided mobile number.
     *
     * @param mobileNumber The mobile number for which password reset is requested.
     * @return             ResponseEntity containing the response DTO.
     */
    @PostMapping("/forgot-password/{mobileNumber}")
    public ResponseEntity<ResponseDTO> forgotPassword(@PathVariable String mobileNumber){
        return passwordService.forgotPassword(mobileNumber);
    }
    /**
     * Endpoint for sending an OTP to the provided mobile number.
     *
     * @param mobileNumber The mobile number to which the OTP will be sent.
     * @return             ResponseEntity containing the response DTO.
     */
    @GetMapping("/send-otp/{mobileNumber}")
    public ResponseEntity<ResponseDTO> sendOTP(@PathVariable(value = "mobileNumber") String mobileNumber){
        return passwordService.sendOTP(mobileNumber);
    }
    /**
     * Endpoint for verifying the OTP provided by the user.
     *
     * @param request A map containing the mobile number and OTP.
     * @return        ResponseEntity containing the response DTO.
     */
    @PostMapping("/verify-otp")
    public  ResponseEntity<ResponseDTO> verifyOTP(@RequestBody Map<String,String> request){
        String mobileNumber = request.get("mobileNumber");
        String otp = request.get("otp");
        return passwordService.verifyOTP(mobileNumber, otp);
    }
    /**
     * Endpoint for resetting the password using the OTP and new password.
     *
     * @param request The request object containing the mobile number, new password, status, and key and hash.
     * @return        ResponseEntity containing the response DTO.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestBody NewPasswordDTO request){
        String status = request.getStatus();
        String mobileNumber = request.getMobileNumber();
        String newPassword = request.getNewPassword();
        String keyAndHash = request.getKeyAndHash();

        try {
            if (Objects.equals(status, "update Password")) {
                boolean didUpdate = passwordService.updatePassword(newPassword, mobileNumber, keyAndHash);
                if(didUpdate){
                    ResponseDTO responseDtO = createDTO(
                            HttpStatus.OK,
                            "Password Updated",
                            "Login again");
                    return ResponseEntity.status(HttpStatus.OK).body(responseDtO);
                }
            }
            ResponseDTO responseDtO = createDTO(
                    HttpStatus.UNAUTHORIZED,
                    "Unauthorized User",
                    "Authentication is required to access this resource");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseDtO);

        }catch(Exception exception){
            log.info(exception.getMessage());
            ResponseDTO responseDtO = createDTO(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Server Down",
                    "LETS TRY AFTER SOMETIME");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDtO);
        }
    }

    private ResponseDTO createDTO(HttpStatus status, String response, String message) {
        return new ResponseDTO(status, response, message) ;
    }
}
