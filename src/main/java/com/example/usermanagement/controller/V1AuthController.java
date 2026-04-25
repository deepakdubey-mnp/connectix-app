package com.example.usermanagement.controller;

import com.example.usermanagement.config.JwtUtil;
import com.example.usermanagement.dto.OtpV1RequestDto;
import com.example.usermanagement.dto.OtpV1ResponseDto;
import com.example.usermanagement.dto.VerifyOtpRequestDto;
import com.example.usermanagement.dto.VerifyOtpResponseDto;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class V1AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * API #1 - Request OTP
     * POST /api/v1/auth/request-otp
     */
    @PostMapping("/request-otp")
    public ResponseEntity<OtpV1ResponseDto> requestOtp(@Valid @RequestBody OtpV1RequestDto request) {
        log.info("V1 OTP request for phone: {}", request.getPhoneNumber());
        OtpV1ResponseDto response = userService.requestOtpV1(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API #2 - Verify OTP
     * POST /api/v1/auth/verify-otp
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponseDto> verifyOtp(@Valid @RequestBody VerifyOtpRequestDto request) {
        log.info("V1 Verify OTP for phone: {}", request.getPhoneNumber());
        User user = userService.verifyOtpV1(request);
        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getRole().name());

        VerifyOtpResponseDto response = VerifyOtpResponseDto.builder()
                .status("success")
                .message("OTP verified successfully")
                .user(VerifyOtpResponseDto.UserBasicDto.builder()
                        .id(user.getId())
                        .phoneNumber(user.getPhoneNumber())
                        .build())
                .token(token)
                .build();

        return ResponseEntity.ok(response);
    }
}

