package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerProductItemDto {
    private String productId;
    private String productImageUrl;
    private String name;
    private String weight;
    private String brand;
    private String category;
    private Double price;
    private String currency;
    private String stock;
}

