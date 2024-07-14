package com.excitel.service;

import com.excitel.dto.FeignResponseDtO;
import com.excitel.dto.ResponseDTO;
import com.excitel.model.Customer;
import org.springframework.http.ResponseEntity;

import javax.management.ServiceNotFoundException;

public interface UserService {

    public ResponseEntity<FeignResponseDtO> validation(String token) throws ServiceNotFoundException;

    public Customer getUserDetail(String mobileNumber) throws ServiceNotFoundException;

    public Customer updateUserDetail(Customer customer);

    public Customer loadUserByUsername(String username);

    public ResponseEntity<ResponseDTO> login(String mobileNumber, String password);

    public boolean resetPassword(String token, String oldPassword, String newPassword) throws ServiceNotFoundException;

    ResponseEntity<ResponseDTO> logout(String token) throws ServiceNotFoundException;
}