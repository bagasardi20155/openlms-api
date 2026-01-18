package com.openlms.api.auth.services;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {
    private final SecureRandom secureRandom = new SecureRandom();
    public String generateOtp() {
        int n = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(n);
    }
}
