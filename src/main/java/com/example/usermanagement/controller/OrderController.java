package com.example.usermanagement.controller;

import com.example.usermanagement.dto.OrderRequestDto;
import com.example.usermanagement.dto.OrderResponseDto;
import com.example.usermanagement.dto.ShopkeeperOrdersPageDto;
import com.example.usermanagement.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * API #7 - Complete Order API
     * POST /api/v1/orders
     */
    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDto> placeOrder(@Valid @RequestBody OrderRequestDto request) {
        log.info("Place order request for shopkeeper id: {}", request.getShopkeeperId());
        OrderResponseDto response = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API #8 - Order Tracking API
     * GET /api/v1/orders/{order_id}
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        log.info("Get order details for id: {}", orderId);
        OrderResponseDto response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * API #9 - Shopkeeper Orders API
     * GET /api/v1/shopkeepers/{shopkeeper_id}/orders
     */
    @GetMapping("/shopkeepers/{shopkeeperId}/orders")
    public ResponseEntity<ShopkeeperOrdersPageDto> getShopkeeperOrders(
            @PathVariable Long shopkeeperId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "created_at") String sort,
            @RequestParam(defaultValue = "desc") String order) {
        log.info("Get orders for shopkeeper id: {}, page: {}, limit: {}", shopkeeperId, page, limit);
        ShopkeeperOrdersPageDto response = orderService.getShopkeeperOrders(shopkeeperId, page, limit, sort, order);
        return ResponseEntity.ok(response);
    }
}

