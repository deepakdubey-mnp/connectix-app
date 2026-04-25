package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerListResponseDto {
    private String status;
    private String message;
    private String contactNumber;
    private String pincode;
    private String category;
    private List<DealerInfoDto> dealers;
    private List<DealerInfoDto> bookmarkedDealers;
    private List<ShopkeeperOrderSummaryDto> shopkeeperOrders;
}

