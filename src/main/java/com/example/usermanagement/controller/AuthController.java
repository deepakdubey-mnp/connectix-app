package com.example.usermanagement.controller;

import com.example.usermanagement.dto.AuthResponseDto;
import com.example.usermanagement.dto.LoginDto;
import com.example.usermanagement.dto.OtpRequestDto;
import com.example.usermanagement.dto.UserRegistrationDto;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import com.example.usermanagement.config.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        log.info("Registration request received for phone: {}", registrationDto.getPhoneNumber());
        return ResponseEntity.ok(userService.registerUser(registrationDto));
    }

    @PostMapping("/otp")
    public ResponseEntity<String> requestOtp(@Valid @RequestBody OtpRequestDto otpRequestDto) {
        log.info("OTP request received for phone: {}", otpRequestDto.getPhoneNumber());
        userService.generateAndSendOtp(otpRequestDto.getPhoneNumber());
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Login request received for phone: {}", loginDto.getPhoneNumber());
        User user = userService.login(loginDto);
        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getRole().name());
        
        AuthResponseDto response = AuthResponseDto.builder()
                .token(token)
                .message("Login successful")
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .build();
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello Ayushi");
    }
}
