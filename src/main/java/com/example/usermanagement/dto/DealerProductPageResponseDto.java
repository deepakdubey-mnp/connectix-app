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
public class DealerProductPageResponseDto {
    private String status;
    private String message;
    private String dealerId;
    private PaginationDto pagination;
    private List<DealerProductItemDto> products;

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
}

