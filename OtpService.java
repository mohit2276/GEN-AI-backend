package com.genai.codeiumapp.service;


import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

    private final JavaMailSender emailSender;
    String username;
    private long otp;
    

    @Autowired
    public OtpService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendOtp(String to,long otp) {
       
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(to);
        message.setTo(to);
        message.setSubject("OTP for Login");
        message.setText("Your OTP is: " + otp);
        emailSender.send(message);
       
    }

    public long generateOtp() {
        SecureRandom random = new SecureRandom();
        return random.nextInt(900000) + 100000L;
    }
    public long getOtp() {
        return otp;
    }
}