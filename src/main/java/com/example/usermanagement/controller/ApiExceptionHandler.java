package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ErrorResponseDto;
import com.example.usermanagement.exception.CommonAttributeAlreadyExistsException;
import com.example.usermanagement.exception.CommonAttributeNotFoundException;
import com.example.usermanagement.exception.DealerProductItemNotFoundException;
import com.example.usermanagement.exception.ForbiddenException;
import com.example.usermanagement.exception.InvalidOtpException;
import com.example.usermanagement.exception.OtpExpiredException;
import com.example.usermanagement.exception.ProductAlreadyExistsException;
import com.example.usermanagement.exception.ProductNotFoundException;
import com.example.usermanagement.exception.ShopDetailsNotFoundException;
import com.example.usermanagement.exception.UserAlreadyExistsException;
import com.example.usermanagement.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        log.error("User not found: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("User Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProductNotFoundException(
            ProductNotFoundException ex, HttpServletRequest request) {
        log.error("Product not found: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Product Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleProductAlreadyExistsException(
            ProductAlreadyExistsException ex, HttpServletRequest request) {
        log.error("Product already exists: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Product Already Exists")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ShopDetailsNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleShopDetailsNotFoundException(
            ShopDetailsNotFoundException ex, HttpServletRequest request) {
        log.error("Shop details not found: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Shop Details Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(DealerProductItemNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleDealerProductItemNotFoundException(
            DealerProductItemNotFoundException ex, HttpServletRequest request) {
        log.error("Dealer product item not found: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Dealer Product Item Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponseDto> handleForbiddenException(
            ForbiddenException ex, HttpServletRequest request) {
        log.error("Forbidden: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Forbidden")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(CommonAttributeNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCommonAttributeNotFoundException(
            CommonAttributeNotFoundException ex, HttpServletRequest request) {
        log.error("Common attribute not found: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Common Attribute Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(CommonAttributeAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleCommonAttributeAlreadyExistsException(
            CommonAttributeAlreadyExistsException ex, HttpServletRequest request) {
        log.error("Common attribute already exists: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Common Attribute Already Exists")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, HttpServletRequest request) {
        log.error("User already exists: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("User Already Exists")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidOtpException(
            InvalidOtpException ex, HttpServletRequest request) {
        log.error("Invalid OTP: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Invalid OTP")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(OtpExpiredException.class)
    public ResponseEntity<ErrorResponseDto> handleOtpExpiredException(
            OtpExpiredException ex, HttpServletRequest request) {
        log.error("OTP expired: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("OTP Expired")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUsernameNotFoundException(
            UsernameNotFoundException ex, HttpServletRequest request) {
        log.error("Username not found: {}", ex.getMessage());
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("User Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.error("Validation error: {}", errorMessage);
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Validation Failed")
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleAllExceptions(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        ErrorResponseDto error = ErrorResponseDto.builder()
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
