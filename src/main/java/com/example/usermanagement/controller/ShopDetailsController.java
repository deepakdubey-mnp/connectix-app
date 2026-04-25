package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ShopDetailsRequestDto;
import com.example.usermanagement.entity.ShopDetails;
import com.example.usermanagement.service.ShopDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/shop-details")
@RequiredArgsConstructor
public class ShopDetailsController {

    private final ShopDetailsService shopDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(ShopDetailsController.class);

    @PostMapping
    public ResponseEntity<ShopDetails> addShopDetails(@RequestBody ShopDetailsRequestDto dto) {
        logger.info("Received request to add shop details: {}", dto);
        ShopDetails saved = shopDetailsService.saveShopDetails(dto);
        logger.info("Shop details saved with id: {}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopDetails> getShopDetailsById(@PathVariable Long id) {
        logger.info("Received request to fetch shop details for id: {}", id);
        ShopDetails shopDetails = shopDetailsService.getShopDetailsById(id);
        return ResponseEntity.ok(shopDetails);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ShopDetails>> getAllShopDetails() {
        logger.info("Received request to fetch all shop details");
        List<ShopDetails> shopDetailsList = shopDetailsService.getAllShopDetails();
        return ResponseEntity.ok(shopDetailsList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShopDetails> updateShopDetails(@PathVariable Long id, @RequestBody ShopDetailsRequestDto dto) {
        logger.info("Received request to update shop details for id {}: {}", id, dto);
        ShopDetails updated = shopDetailsService.updateShopDetails(id, dto);
        logger.info("Shop details updated for id: {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShopDetails(@PathVariable Long id) {
        logger.info("Received request to delete shop details for id: {}", id);
        shopDetailsService.deleteShopDetails(id);
        logger.info("Shop details deleted for id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
