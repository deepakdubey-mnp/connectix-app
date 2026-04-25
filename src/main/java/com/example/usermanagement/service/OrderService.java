package com.example.usermanagement.service;

import com.example.usermanagement.dto.OrderItemRequestDto;
import com.example.usermanagement.dto.OrderItemResponseDto;
import com.example.usermanagement.dto.OrderRequestDto;
import com.example.usermanagement.dto.OrderResponseDto;
import com.example.usermanagement.dto.ShopkeeperOrdersPageDto;
import com.example.usermanagement.entity.Order;
import com.example.usermanagement.entity.OrderItem;
import com.example.usermanagement.entity.OrderStatus;
import com.example.usermanagement.entity.ProductDetails;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.ForbiddenException;
import com.example.usermanagement.exception.OrderNotFoundException;
import com.example.usermanagement.exception.ProductNotFoundException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.OrderRepository;
import com.example.usermanagement.repository.ProductDetailsRepository;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductDetailsRepository productDetailsRepository;

    /**
     * POST /api/v1/orders - Place a new order
     */
    @Transactional
    public OrderResponseDto placeOrder(OrderRequestDto request) {
        log.info("Placing order for shopkeeper id: {} with dealer id: {}", request.getShopkeeperId(), request.getDealerId());

        User shopkeeper = userRepository.findById(request.getShopkeeperId())
                .orElseThrow(() -> new UserNotFoundException("Shopkeeper not found with id: " + request.getShopkeeperId()));

        User dealer = userRepository.findById(request.getDealerId())
                .orElseThrow(() -> new UserNotFoundException("Dealer not found with id: " + request.getDealerId()));

        if (dealer.getRole() != Role.DEALER) {
            throw new ForbiddenException("Specified user is not a dealer");
        }

        Order order = Order.builder()
                .shopkeeper(shopkeeper)
                .dealer(dealer)
                .totalAmount(request.getTotalAmount())
                .specialInstructions(request.getSpecialInstructions())
                .deliveryAddress(shopkeeper.getShopAddress())
                .items(new ArrayList<>())
                .build();

        // Build order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemRequestDto itemDto : request.getItems()) {
            ProductDetails product = productDetailsRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + itemDto.getProductId()));

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productDetails(product)
                    .quantity(itemDto.getQuantity())
                    .unitPrice(itemDto.getUnitPrice())
                    .totalPrice(itemDto.getTotalPrice())
                    .build();
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully with id: {}", savedOrder.getId());

        return toOrderResponseDto("Order placed successfully", savedOrder);
    }

    /**
     * GET /api/v1/orders/{order_id} - Get order by ID
     */
    public OrderResponseDto getOrderById(Long orderId) {
        log.info("Fetching order with id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        return toOrderResponseDto("Order details fetched successfully", order);
    }

    /**
     * GET /api/v1/shopkeepers/{shopkeeper_id}/orders - Get orders for shopkeeper with pagination
     */
    public ShopkeeperOrdersPageDto getShopkeeperOrders(Long shopkeeperId, int page, int limit, String sort, String order) {
        log.info("Fetching orders for shopkeeper id: {}", shopkeeperId);

        User shopkeeper = userRepository.findById(shopkeeperId)
                .orElseThrow(() -> new UserNotFoundException("Shopkeeper not found with id: " + shopkeeperId));

        Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = "created_at".equalsIgnoreCase(sort) || sort == null ? "createdAt" : sort;

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(direction, sortField));
        Page<Order> ordersPage = orderRepository.findByShopkeeper(shopkeeper, pageable);

        List<ShopkeeperOrdersPageDto.OrderSummaryDto> orderSummaries = ordersPage.getContent().stream()
                .map(this::toOrderSummaryDto)
                .collect(Collectors.toList());

        return ShopkeeperOrdersPageDto.builder()
                .status("success")
                .message("Orders fetched successfully")
                .shopkeeperId("sk_" + shopkeeperId)
                .pagination(ShopkeeperOrdersPageDto.PaginationDto.builder()
                        .page(page)
                        .limit(limit)
                        .totalRecords(ordersPage.getTotalElements())
                        .totalPages(ordersPage.getTotalPages())
                        .build())
                .orders(orderSummaries)
                .build();
    }

    // ===== Helpers =====

    private OrderResponseDto toOrderResponseDto(String message, Order order) {
        List<OrderItemResponseDto> itemDtos = order.getItems().stream()
                .map(item -> OrderItemResponseDto.builder()
                        .productId(item.getProductDetails() != null ? item.getProductDetails().getProductId() : null)
                        .productImageUrl(item.getProductDetails() != null ? item.getProductDetails().getImageLocation() : null)
                        .name(item.getProductDetails() != null ? item.getProductDetails().getProductName() : null)
                        .quantity(item.getQuantity())
                        .totalPrice(item.getTotalPrice())
                        .currency("INR")
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDto.builder()
                .status("success")
                .message(message)
                .orderId("ord_" + order.getId())
                .dealerId("dl_" + (order.getDealer() != null ? order.getDealer().getId() : null))
                .shopkeeperId("sk_" + (order.getShopkeeper() != null ? order.getShopkeeper().getId() : null))
                .address(order.getDeliveryAddress())
                .items(itemDtos)
                .totalAmount(order.getTotalAmount())
                .currency("INR")
                .orderStatus(order.getOrderStatus().name())
                .createdAt(order.getCreatedAt())
                .build();
    }

    private ShopkeeperOrdersPageDto.OrderSummaryDto toOrderSummaryDto(Order order) {
        List<ShopkeeperOrdersPageDto.OrderItemBriefDto> itemBriefs = order.getItems().stream()
                .map(item -> ShopkeeperOrdersPageDto.OrderItemBriefDto.builder()
                        .productId(item.getProductDetails() != null ? item.getProductDetails().getProductId() : null)
                        .name(item.getProductDetails() != null ? item.getProductDetails().getProductName() : null)
                        .quantity(item.getQuantity())
                        .price(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return ShopkeeperOrdersPageDto.OrderSummaryDto.builder()
                .orderId("ord_" + order.getId())
                .totalAmount(order.getTotalAmount())
                .currency("INR")
                .orderStatus(order.getOrderStatus().name())
                .createdAt(order.getCreatedAt())
                .items(itemBriefs)
                .build();
    }
}

