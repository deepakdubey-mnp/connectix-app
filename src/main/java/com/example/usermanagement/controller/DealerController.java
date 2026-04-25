package com.example.usermanagement.controller;

import com.example.usermanagement.dto.DealerListResponseDto;
import com.example.usermanagement.dto.DealerProductPageResponseDto;
import com.example.usermanagement.service.DealerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dealers")
@RequiredArgsConstructor
@Slf4j
public class DealerController {

    private final DealerService dealerService;

    /**
     * API #5 - Dealer Data API
     * GET /api/v1/dealers?pincode=&category=&shopkeeper_id=
     */
    @GetMapping
    public ResponseEntity<DealerListResponseDto> getDealers(
            @RequestParam(required = false) String pincode,
            @RequestParam(required = false) String category,
            @RequestParam(name = "shopkeeper_id", required = false) String shopkeeperId) {
        log.info("Get dealers - pincode: {}, category: {}, shopkeeperId: {}", pincode, category, shopkeeperId);
        DealerListResponseDto response = dealerService.getDealers(pincode, category, shopkeeperId);
        return ResponseEntity.ok(response);
    }

    /**
     * API #6 - Dealer Product Data API
     * GET /api/v1/dealers/{dealer_id}/products
     */
    @GetMapping("/{dealerId}/products")
    public ResponseEntity<DealerProductPageResponseDto> getDealerProducts(
            @PathVariable Long dealerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "price") String sort,
            @RequestParam(defaultValue = "asc") String order) {
        log.info("Get dealer products for dealer id: {}, page: {}, limit: {}", dealerId, page, limit);
        DealerProductPageResponseDto response = dealerService.getDealerProducts(dealerId, page, limit, sort, order);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/dealers/{dealer_id}/bookmark - Bookmark a dealer
     */
    @PostMapping("/{dealerId}/bookmark")
    public ResponseEntity<Void> bookmarkDealer(@PathVariable Long dealerId) {
        log.info("Bookmark dealer id: {}", dealerId);
        dealerService.bookmarkDealer(dealerId);
        return ResponseEntity.ok().build();
    }

    /**
     * DELETE /api/v1/dealers/{dealer_id}/bookmark - Remove dealer bookmark
     */
    @DeleteMapping("/{dealerId}/bookmark")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long dealerId) {
        log.info("Remove bookmark for dealer id: {}", dealerId);
        dealerService.removeBookmark(dealerId);
        return ResponseEntity.noContent().build();
    }
}

