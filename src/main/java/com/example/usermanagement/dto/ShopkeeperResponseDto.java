package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopkeeperResponseDto {
    private String status;
    private String message;
    private String shopkeeperId;
    private ShopkeeperDataDto data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShopkeeperDataDto {
        private String ownerName;
        private String shopName;
        private String category;
        private String gstNumber;
        private String address;
        private String pincode;
    }
}

