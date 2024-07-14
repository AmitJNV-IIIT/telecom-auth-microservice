package com.excitel.serviceimpl;//NOSONAR

import com.excitel.dto.ResponseDTO;
import com.excitel.encryption.PasswordDecoder;
import com.excitel.model.Customer;
import com.excitel.repository.AuthDynamoRepository;
import com.excitel.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 Service implementation for managing customers.
 */
@Service
public class CustomerServiceImpl implements CustomerService {


    @Autowired//NOSONAR
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordDecoder passwordDecoder;

    @Autowired//NOSONAR
    private AuthDynamoRepository authDynamoRepository;

    /**
     Method to create a ResponseDTO
     @param httpStatus HTTP status code
     @param response Response message
     @param message Detail message
     @return ResponseDTO object
     */
    private ResponseDTO createDtO(HttpStatus httpStatus, String response, String message) {
        return new ResponseDTO(httpStatus, response, message);
    }


    /**
     Method to register a new customer.
     @param customer The customer to be registered
     @return ResponseEntity with the status and message of the operation
     */
    @Override
    public ResponseEntity<ResponseDTO> registerCustomer(Customer customer) {
        //encoded password
        String password = passwordDecoder.decodePassword(customer.getPassword());

        customer.setPassword(passwordEncoder.encode(password));
        boolean status = authDynamoRepository.saveUser(customer);//NOSONAR

        ResponseDTO response = createDtO(HttpStatus.CREATED, "User registered successfully!", "Kindly Login");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}