package com.excitel.service;
import com.excitel.dto.ResponseDTO;
import com.excitel.model.Customer;
import org.springframework.http.ResponseEntity;


public interface CustomerService {
    ResponseEntity<ResponseDTO> registerCustomer(Customer customer);
}
