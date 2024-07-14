package com.excitel.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequestDTO {
    private String mobileNumber; //NOSONAR
    private String password; //NOSONAR

}
