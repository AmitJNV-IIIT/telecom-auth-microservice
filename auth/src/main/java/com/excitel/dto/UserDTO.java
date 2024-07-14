package com.excitel.dto;

import com.excitel.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter //NOSONAR
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
        private HttpStatus status; //NOSONAR
        private String message; //NOSONAR
        private Customer data; //NOSONAR
}
