package com.excitel.encryption;


import lombok.Setter;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Base64;

@Component
public class PasswordDecoder {
    Logger logger;

    @Value("${secretKey}")
    private String secretKey;

    /**
     * Decode the encrypted password using the secret key.
     *
     * @param encryptedPassword The encrypted password to decode.
     * @return The decoded password.
     * @throws IllegalArgumentException if the encrypted password is invalid.
     */
    public String decodePassword(String encryptedPassword)  {

        byte[] decodedPasswordBytes = Base64.getDecoder().decode(encryptedPassword.getBytes());
        String decodedPassword  = new String(decodedPasswordBytes);
        if (decodedPassword.length() < secretKey.length()) {
            throw new NullPointerException("Invalid encrypted password");
        }

        //actual password
        return decodedPassword.substring(0,decodedPassword.length()-secretKey.length());

    }
}