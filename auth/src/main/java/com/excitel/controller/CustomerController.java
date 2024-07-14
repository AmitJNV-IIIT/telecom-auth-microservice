package com.excitel.controller;
import java.util.Arrays;

import com.excitel.dto.ResponseDTO;
import com.excitel.exception.custom.InvalidRequestBodyException;
import com.excitel.model.Customer;
import com.excitel.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

/**
 * Controller class for handling customer-related endpoints.
 */
@RestController
@RequestMapping("/api/v2")
@CrossOrigin("*")
public class CustomerController {

    @Autowired//NOSONAR
    private CustomerService customerService;

    /**
     * Endpoint for registering a new user.
     *
     * @param customer The customer object containing registration details.
     * @return         ResponseEntity containing the response DTO.
     */
    @PostMapping("/auth/register")
    public ResponseEntity<ResponseDTO> registerUser(@RequestBody Customer customer) {

        if (isValidCustomer(customer)) return customerService.registerCustomer(customer);
        else throw new InvalidRequestBodyException("Invalid Request Body");
    }
    /**
     * Validates the customer object for registration.
     *
     * @param customer The customer object to validate.
     * @return         True if the customer object is valid, otherwise false.
     */
    private boolean isValidCustomer(Customer customer) {
        return Arrays.asList(
                customer.getPassword(),
                customer.getEmail(),
                customer.getMobileNumber(),
                customer.getName(),
                customer.getAddress(),
                customer.getSimType(),
                customer.getPinCode(),
                customer.getCountry(),
                customer.getState()
        ).stream().noneMatch(this::isNullOrEmpty);
    }
    /**
     * Checks if a string is null or empty.
     *
     * @param str The string to check.
     * @return    True if the string is null or empty, otherwise false.
     */
    boolean isNullOrEmpty(String str) {return str == null || str.isEmpty();}
}