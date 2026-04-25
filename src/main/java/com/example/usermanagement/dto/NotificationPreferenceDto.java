package com.example.usermanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferenceDto {
    private Boolean offersEnabled;
    private Boolean saleEnabled;
    private Boolean orderStatusEnabled;
    private Boolean limitedStockEnabled;
    private Boolean systemDowntimeEnabled;
    private Boolean personalizedEnabled;
}
