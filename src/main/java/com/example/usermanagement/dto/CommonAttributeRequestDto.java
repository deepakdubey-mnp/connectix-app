package com.example.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonAttributeRequestDto {

    @NotBlank(message = "Key is required")
    private String key;

    @NotBlank(message = "Language is required")
    private String language;

    @NotBlank(message = "Value is required")
    private String value;
}
