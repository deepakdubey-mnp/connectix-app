package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {
    private String productId;
    private String productImageUrl;
    private String name;
    private Integer quantity;
    private Double totalPrice;
    private String currency;
}

