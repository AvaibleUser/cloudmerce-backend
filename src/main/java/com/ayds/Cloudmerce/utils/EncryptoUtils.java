package com.ayds.Cloudmerce.utils;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class EncryptoUtils {

    /**
     * Encrypt the received value and return the hashed password
     *
     * @param originalPassword
     * @return hashedPassword
     */
    public String encryptPassword(String originalPassword) {
        BCryptPasswordEncoder encoderPW = new BCryptPasswordEncoder(16);
        String passworEncoded = encoderPW.encode(originalPassword);
        return passworEncoded;
    }


    /**
     * Verify the original value (not encrypted) with the hashed value
     *
     * @param originalValue
     * @param hashedValue
     * @return true/false if value is valid
     */
    public boolean verifyPassword(String originalValue, String hashedValue) {
        BCryptPasswordEncoder encoderPW = new BCryptPasswordEncoder(16);
        return encoderPW.matches(originalValue, hashedValue);
    }
}
