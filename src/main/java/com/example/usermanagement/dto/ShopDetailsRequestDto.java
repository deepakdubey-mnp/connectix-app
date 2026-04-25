package com.example.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailsRequestDto {
    @NotBlank(message = "Owner Name is required")
    private String ownerName;

    @NotBlank(message = "Shop Name is required")
    private String shopName;
    private String category;
    private String gstNumber;
    private String address;
    @NotBlank(message = "Pincode is required")
    private String pincode;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Boolean active;


}

