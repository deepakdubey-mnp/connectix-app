package com.example.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpV1RequestDto {

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String channel; // sms or email
}

