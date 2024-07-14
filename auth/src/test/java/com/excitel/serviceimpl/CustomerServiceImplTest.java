package com.excitel.serviceimpl;

import com.excitel.dto.ResponseDTO;
import com.excitel.encryption.PasswordDecoder;
import com.excitel.model.Customer;
import com.excitel.repository.AuthDynamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordDecoder passwordDecoder;

    @Mock
    private AuthDynamoRepository authDynamoRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void registerCustomerTest() {
        // Arrange
        String password = "password";
        String decodedPassword = "decodedPassword";
        String encodedPassword = "encodedPassword";
        Customer customer = new Customer(); // initialise with appropriate data
        customer.setPassword(password);
        ResponseDTO responseDTO = new ResponseDTO(HttpStatus.CREATED, "User registered successfully!", "Kindly Login");

        when(passwordDecoder.decodePassword(password)).thenReturn(decodedPassword);
        when(passwordEncoder.encode(decodedPassword)).thenReturn(encodedPassword);
        when(authDynamoRepository.saveUser(customer)).thenReturn(true);

        // Act
        ResponseEntity<ResponseDTO> result = customerService.registerCustomer(customer);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        verify(passwordDecoder, times(1)).decodePassword(password);
        verify(passwordEncoder, times(1)).encode(decodedPassword);
        verify(authDynamoRepository, times(1)).saveUser(customer);
    }
}
