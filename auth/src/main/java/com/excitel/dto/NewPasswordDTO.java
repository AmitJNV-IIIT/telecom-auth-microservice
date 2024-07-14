package com.excitel.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewPasswordDTO {
    private String status; //NOSONAR
    private String mobileNumber; //NOSONAR
    private String keyAndHash; //NOSONAR
    private String newPassword; //NOSONAR
}
