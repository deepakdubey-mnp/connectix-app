package com.example.usermanagement.dto;

import com.example.usermanagement.entity.ProductSubType;
import com.example.usermanagement.entity.ProductType;
import com.example.usermanagement.entity.QuantityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsRequestDto {

    private Long id;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String productDescription;

    @NotBlank(message = "Company is required")
    private String company;

    @NotNull(message = "Product type is required")
    private ProductType productType;

    private ProductSubType productSubType;

    private String imageLocation;

    private QuantityType quantityType;
}
