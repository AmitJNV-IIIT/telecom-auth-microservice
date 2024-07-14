package com.excitel.controller;

import com.excitel.dto.*;
import com.excitel.exception.custom.CustomDynamoDbException;
import com.excitel.model.Customer;
import com.excitel.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import javax.management.ServiceNotFoundException;
import java.util.Arrays;

/**
 * Controller class for handling user-related endpoints.
 */
@RestController
@RequestMapping("/api/v2/auth")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    /**
     * Endpoint for checking the health status of the application.
     *
     * @return The health status message.
     */


    @GetMapping("/health")
    public String initalisation(){
        return "live";
    }

    /**
     * Endpoint for validating the provided JWT token.
     *
     * @param token The JWT token to validate.
     * @return      ResponseEntity containing the FeignResponseDTO.
     */
    @GetMapping("/check-token")
    public ResponseEntity<FeignResponseDtO> isValid(@RequestHeader("Authorization") String token) throws ServiceNotFoundException {
            return userService.validation(token);
    }
    /**
     * Endpoint for user login.
     *
     * @param request The login request DTO containing mobile number and password.
     * @return        ResponseEntity containing the response DTO.
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO> login(@RequestBody JwtRequestDTO request) {


            return userService.login(request.getMobileNumber(), request.getPassword());

    }
    /**
     * Endpoint for fetching user details.
     *
     * @param token The JWT token for authentication.
     * @return      ResponseEntity containing the UserDTO.
     */
    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserDetails(@RequestHeader("Authorization") String token) throws ServiceNotFoundException {

            Customer user = userService.getUserDetail(token);
            UserDTO userDtO = convertToDtO(HttpStatus.OK, "Details fetched successfully.", user);
            return ResponseEntity.status(HttpStatus.OK).body(userDtO);
    }
    /**
     * Endpoint for updating user details.
     *
     * @param user The user details to be updated.
     * @return     ResponseEntity containing the updated UserDTO.
     */
    @PutMapping("/user")
    public ResponseEntity<UserDTO> updateUserDetails(@RequestBody Customer user){
        if(isValidResponse(user)){
            Customer updatedUser = userService.updateUserDetail(user);
            UserDTO userDtO = convertToDtO(HttpStatus.OK, "updated user details set successfully.", updatedUser);
            return ResponseEntity.status(HttpStatus.OK).body(userDtO);
        }
        throw new CustomDynamoDbException("Invalid Request Body");
    }
    public boolean isValidResponse(Customer customer) {
        return Arrays.asList(
                customer.getEmail(),
                customer.getName(),
                customer.getAddress(),
                customer.getPinCode(),
                customer.getState()
        ).stream().noneMatch(this::isNullOrEmpty);
    }
    boolean isNullOrEmpty(String str) {return str == null || str.isEmpty();}

    /**
     * Endpoint for resetting password after login.
     *
     * @param token   The JWT token for authentication.
     * @param request The reset password request DTO containing old and new password.
     * @return        ResponseEntity containing the response DTO.
     */
    @PostMapping("/reset-password-login")
    public ResponseEntity<ResponseDTO> resetPassword(@RequestHeader("Authorization") String token,
                                                     @RequestBody ResetPasswordDTO request) throws ServiceNotFoundException {

            userService.resetPassword(token, request.getOldPassword(), request.getNewPassword());
            ResponseDTO response = createDTO(HttpStatus.OK, "Password Updated", "Success");
            return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO> logout(@RequestHeader("Authorization") String token) throws ServiceNotFoundException {

        return userService.logout(token);
    }
    /**
     * Helper method to convert Customer object to UserDTO.
     *
     * @param httpStatus The HTTP status of the response.
     * @param message    The response message.
     * @param user       The Customer object.
     * @return           The UserDTO.
     */
    private UserDTO convertToDtO(HttpStatus httpStatus, String message, Customer user) {
        return new UserDTO(httpStatus, message, user);
    }
    /**
     * Helper method to create a response DTO.
     *
     * @param status   The HTTP status of the response.
     * @param response The response message.
     * @param message  The message explaining the response.
     * @return         The response DTO.
     */
    private ResponseDTO createDTO(HttpStatus status, String response, String message) {
        return new ResponseDTO(status, response, message) ;
    }
}
