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
public class ShopkeeperOrdersPageDto {
    private String status;
    private String message;
    private String shopkeeperId;
    private PaginationDto pagination;
    private List<OrderSummaryDto> orders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDto {
        private int page;
        private int limit;
        private long totalRecords;
        private int totalPages;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummaryDto {
        private String orderId;
        private Double totalAmount;
        private String currency;
        private String orderStatus;
        private LocalDateTime createdAt;
        private List<OrderItemBriefDto> items;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemBriefDto {
        private String productId;
        private String name;
        private Integer quantity;
        private Double price;
    }
}

