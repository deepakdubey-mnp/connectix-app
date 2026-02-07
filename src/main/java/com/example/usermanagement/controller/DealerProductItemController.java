package com.example.usermanagement.controller;

import com.example.usermanagement.dto.DealerProductItemRequestDto;
import com.example.usermanagement.dto.DealerProductItemUpdateDto;
import com.example.usermanagement.entity.DealerProductItem;
import com.example.usermanagement.service.DealerProductItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dealer/product-items")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('DEALER')")
public class DealerProductItemController {

    private final DealerProductItemService dealerProductItemService;

    @PostMapping
    public ResponseEntity<DealerProductItem> add(@Valid @RequestBody DealerProductItemRequestDto dto) {
        log.info("Add dealer product item request for dealerProductId: {}", dto.getDealerProductId());
        DealerProductItem created = dealerProductItemService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DealerProductItem> updateQuantityAndPrice(
            @PathVariable Long id,
            @Valid @RequestBody DealerProductItemUpdateDto dto) {
        log.info("Update dealer product item request for id: {}", id);
        DealerProductItem updated = dealerProductItemService.updateQuantityAndPrice(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DealerProductItem> getById(@PathVariable Long id) {
        DealerProductItem item = dealerProductItemService.getById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<DealerProductItem>> getByDealerProductId(
            @RequestParam Long dealerProductId) {
        List<DealerProductItem> items = dealerProductItemService.getByDealerProductId(dealerProductId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Delete dealer product item request for id: {}", id);
        dealerProductItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
