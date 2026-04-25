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
public class ShopkeeperOrderSummaryDto {
    private String orderId;
    private String dealerId;
    private String status;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemSummaryDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemSummaryDto {
        private String itemName;
        private String itemWeight;
        private Integer quantity;
        private Double price;
    }
}

