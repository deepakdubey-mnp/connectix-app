package com.example.usermanagement.service;

import com.example.usermanagement.dto.LoginDto;
import com.example.usermanagement.dto.UserRegistrationDto;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.InvalidOtpException;
import com.example.usermanagement.exception.OtpExpiredException;
import com.example.usermanagement.exception.UserAlreadyExistsException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        log.info("Registering user with phone: {}", registrationDto.getPhoneNumber());
        
       if (userRepository.findByPhoneNumber(registrationDto.getPhoneNumber()).isPresent()) {
            throw new UserAlreadyExistsException("Phone number already in use");
        }

        User user = User.builder()
                .email(registrationDto.getEmail())
                .phoneNumber(registrationDto.getPhoneNumber())
                .role(registrationDto.getRole() != null ? registrationDto.getRole() : Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional
    public String generateAndSendOtp(String phoneNumber) {
        log.info("Generating OTP for phone number: {}", phoneNumber);
        
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        String sentOtp = otpService.sendOtp(phoneNumber, otp);
        log.info("OTP sent successfully to phone number: {}", phoneNumber);
        return sentOtp;
    }

    @Transactional
    public User login(LoginDto loginDto) {
        log.info("Attempting login for phone number: {}", loginDto.getPhoneNumber());
        
        User user = userRepository.findByPhoneNumber(loginDto.getPhoneNumber())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getOtp() == null || !user.getOtp().equals(loginDto.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }

        if (user.getOtpExpiryTime() == null || user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }

        // Clear OTP after successful login
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        User savedUser = userRepository.save(user);
        log.info("Login successful for phone number: {}", loginDto.getPhoneNumber());
        return savedUser;
    }

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with phone: " + phoneNumber));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getPhoneNumber())
                .password("") // No password
                .roles(user.getRole().name())
                .build();
    }
}
