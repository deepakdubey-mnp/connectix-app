package com.example.usermanagement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotNull(message = "Shopkeeper ID is required")
    private Long shopkeeperId;

    @NotNull(message = "Dealer ID is required")
    private Long dealerId;

    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequestDto> items;

    @NotNull(message = "Total amount is required")
    private Double totalAmount;

    private String specialInstructions;
}

