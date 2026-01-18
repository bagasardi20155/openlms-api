package com.openlms.api.auth.services;

import org.springframework.stereotype.Service;

@Service
public class SendOtpService {
    // temporary. improve for send otp via email
    public void sendOtpViaEmail(String to, String otp) {
        System.out.println("CHECK HERE FOR OTP atas nama " + to + " : " + otp);
    }
}
