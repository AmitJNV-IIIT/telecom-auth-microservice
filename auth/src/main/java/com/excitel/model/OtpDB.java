package com.excitel.model;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OtpDB {
    private String mobileNumber; //NOSONAR
    private String otp; //NOSONAR
    private String clientID; //NOSONAR
    private String hashKey; //NOSONAR
}
