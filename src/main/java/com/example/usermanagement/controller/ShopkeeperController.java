package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ShopDetailsRequestDto;
import com.example.usermanagement.dto.ShopkeeperResponseDto;
import com.example.usermanagement.service.ShopDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shopkeepers")
@RequiredArgsConstructor
@Slf4j
public class ShopkeeperController {

    private final ShopDetailsService shopDetailsService;

    /**
     * API #3 - Shopkeeper Information Store
     * POST /api/v1/shopkeepers
     */
    @PostMapping
    public ResponseEntity<ShopkeeperResponseDto> createShopkeeper(
            @Valid @RequestBody ShopDetailsRequestDto dto) {
        log.info("V1 Create shopkeeper request: {}", dto.getShopName());
        ShopkeeperResponseDto response = shopDetailsService.saveShopkeeperV1(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API #4 - Update Shopkeeper Information
     * PUT /api/v1/shopkeepers/{shopkeeper_id}
     */
    @PutMapping("/{shopkeeperId}")
    public ResponseEntity<ShopkeeperResponseDto> updateShopkeeper(
            @PathVariable Long shopkeeperId,
            @Valid @RequestBody ShopDetailsRequestDto dto) {
        log.info("V1 Update shopkeeper request for id: {}", shopkeeperId);
        ShopkeeperResponseDto response = shopDetailsService.updateShopkeeperV1(shopkeeperId, dto);
        return ResponseEntity.ok(response);
    }
}

