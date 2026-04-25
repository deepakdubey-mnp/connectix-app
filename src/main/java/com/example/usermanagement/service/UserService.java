package com.example.usermanagement.service;
import com.example.usermanagement.dto.LoginDto;
import com.example.usermanagement.dto.OtpV1RequestDto;
import com.example.usermanagement.dto.OtpV1ResponseDto;
import com.example.usermanagement.dto.UserRegistrationDto;
import com.example.usermanagement.dto.VerifyOtpRequestDto;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.InvalidOtpException;
import com.example.usermanagement.exception.InvalidTransactionException;
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
import java.util.UUID;
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
    /**
     * V1 Request OTP - creates user if not exists, generates transactionId
     */
    @Transactional
    public OtpV1ResponseDto requestOtpV1(OtpV1RequestDto request) {
        log.info("V1 OTP request for phone: {}", request.getPhoneNumber());
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseGet(() -> {
                    log.info("Creating new user for phone: {}", request.getPhoneNumber());
                    User newUser = User.builder()
                            .phoneNumber(request.getPhoneNumber())
                            .role(Role.SHOPKEEPER)
                            .build();
                    return userRepository.save(newUser);
                });
        String otp = otpService.generateOtp();
        String transactionId = "txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        user.setOtp(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        user.setTransactionId(transactionId);
        userRepository.save(user);
        otpService.sendOtp(request.getPhoneNumber(), otp);
        log.info("V1 OTP sent. transactionId: {}", transactionId);
        return OtpV1ResponseDto.builder()
                .status("success")
                .message("OTP has been sent to your mobile number")
                .transactionId(transactionId)
                .build();
    }
    /**
     * V1 Verify OTP - validates transactionId + OTP, returns user
     */
    @Transactional
    public User verifyOtpV1(VerifyOtpRequestDto request) {
        log.info("V1 Verify OTP for phone: {}", request.getPhoneNumber());
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.getTransactionId() == null || !user.getTransactionId().equals(request.getTransactionId())) {
            throw new InvalidTransactionException("Invalid transaction ID");
        }
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new InvalidOtpException("Invalid OTP");
        }
        if (user.getOtpExpiryTime() == null || user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }
        user.setOtp(null);
        user.setOtpExpiryTime(null);
        user.setTransactionId(null);
        user.setAuthenticated(true);
        User savedUser = userRepository.save(user);
        log.info("V1 OTP verified for phone: {}", request.getPhoneNumber());
        return savedUser;
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
                .password("")
                .roles(user.getRole().name())
                .build();
    }
}
