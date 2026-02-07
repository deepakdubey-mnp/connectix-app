package com.example.usermanagement.controller;

import com.example.usermanagement.dto.ProductDetailsRequestDto;
import com.example.usermanagement.entity.ProductDetails;
import com.example.usermanagement.service.ProductDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductDetailsController {

    private final ProductDetailsService productDetailsService;

    @PostMapping
    public ResponseEntity<ProductDetails> add(@Valid @RequestBody ProductDetailsRequestDto dto) {
        log.info("Add product request: {} - {}", dto.getProductName(), dto.getCompany());
        ProductDetails created = productDetailsService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDetails> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDetailsRequestDto dto) {
        log.info("Update product request for id: {}", id);
        ProductDetails updated = productDetailsService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Delete product request for id: {}", id);
        productDetailsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetails> getById(@PathVariable Long id) {
        ProductDetails product = productDetailsService.getById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    public ResponseEntity<List<ProductDetails>> getAll() {
        List<ProductDetails> products = productDetailsService.getAll();
        return ResponseEntity.ok(products);
    }
}
