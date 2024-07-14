package com.excitel.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {
    private HttpStatus status; //NOSONAR
    private String message; //NOSONAR
    private String response; //NOSONAR

}