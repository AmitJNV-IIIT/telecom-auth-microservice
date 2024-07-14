package com.excitel.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@NoArgsConstructor
public class FeignResponseDtO {
    private HttpStatus status; //NOSONAR
    private String message; //NOSONAR
    private String mobileNumber; //NOSONAR
    private String email; //NOSONAR
    private String role; //NOSONAR
    private String simType; //NOSONAR

    public FeignResponseDtO(HttpStatus status, String errorMessage, String phoneNumber, String email, String role, String simType ) {
        this.status = status;
        this.message = errorMessage;
        this.mobileNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.simType=simType;


    }

}
