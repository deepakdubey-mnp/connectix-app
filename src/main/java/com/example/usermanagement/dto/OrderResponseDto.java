package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private String status;
    private String message;
    private String orderId;
    private String dealerId;
    private String shopkeeperId;
    private String address;
    private List<OrderItemResponseDto> items;
    private Double totalAmount;
    private String currency;
    private String orderStatus;
    private LocalDateTime createdAt;
}

