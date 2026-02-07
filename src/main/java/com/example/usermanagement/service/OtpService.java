package com.example.usermanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public String sendOtp(String phoneNumber, String otp) {
        // Dummy implementation: Log the OTP
        logger.info("Sending OTP {} to phone number {}", otp, phoneNumber);
        return otp;
    }
}
