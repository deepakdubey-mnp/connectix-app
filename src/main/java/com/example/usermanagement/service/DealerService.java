package com.example.usermanagement.service;

import com.example.usermanagement.dto.DealerInfoDto;
import com.example.usermanagement.dto.DealerListResponseDto;
import com.example.usermanagement.dto.DealerProductItemDto;
import com.example.usermanagement.dto.DealerProductPageResponseDto;
import com.example.usermanagement.dto.ShopkeeperOrderSummaryDto;
import com.example.usermanagement.entity.BookmarkedDealer;
import com.example.usermanagement.entity.DealerProduct;
import com.example.usermanagement.entity.DealerProductItem;
import com.example.usermanagement.entity.Order;
import com.example.usermanagement.entity.ProductSubType;
import com.example.usermanagement.entity.ProductType;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.ForbiddenException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.BookmarkedDealerRepository;
import com.example.usermanagement.repository.DealerProductItemRepository;
import com.example.usermanagement.repository.DealerProductRepository;
import com.example.usermanagement.repository.OrderRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealerService {

    private final UserRepository userRepository;
    private final DealerProductRepository dealerProductRepository;
    private final DealerProductItemRepository dealerProductItemRepository;
    private final BookmarkedDealerRepository bookmarkedDealerRepository;
    private final OrderRepository orderRepository;

    /**
     * GET /api/v1/dealers?pincode=&category=&shopkeeper_id=
     */
    public DealerListResponseDto getDealers(String pincode, String category, String shopkeeperId) {
        log.info("Fetching dealers - pincode: {}, category: {}, shopkeeperId: {}", pincode, category, shopkeeperId);

        Integer pincodeInt = null;
        if (pincode != null && !pincode.isBlank()) {
            try {
                pincodeInt = Integer.parseInt(pincode);
            } catch (NumberFormatException e) {
                log.warn("Invalid pincode format: {}", pincode);
            }
        }

        ProductType productType = null;
        if (category != null && !category.isBlank()) {
            try {
                productType = ProductType.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Unknown category: {}", category);
            }
        }

        List<User> dealers = userRepository.findDealersByPincodeAndCategory(Role.DEALER, pincodeInt, productType);

        List<DealerInfoDto> dealerDtos = dealers.stream()
                .map(this::toDealerInfoDto)
                .collect(Collectors.toList());

        // Fetch bookmarked dealers for shopkeeper
        List<DealerInfoDto> bookmarkedDealerDtos = new ArrayList<>();
        List<ShopkeeperOrderSummaryDto> orderSummaries = new ArrayList<>();
        String contactNumber = null;

        User shopkeeper = getAuthenticatedUserOptional();
        if (shopkeeper != null) {
            contactNumber = shopkeeper.getPhoneNumber();

            List<BookmarkedDealer> bookmarks = bookmarkedDealerRepository.findByShopkeeper(shopkeeper);
            bookmarkedDealerDtos = bookmarks.stream()
                    .map(bm -> toDealerInfoDto(bm.getDealer()))
                    .collect(Collectors.toList());

            // Fetch recent orders (last 5)
            Pageable recentOrdersPage = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Order> recentOrders = orderRepository.findByShopkeeper(shopkeeper, recentOrdersPage);
            orderSummaries = recentOrders.getContent().stream()
                    .map(this::toOrderSummary)
                    .collect(Collectors.toList());
        }

        return DealerListResponseDto.builder()
                .status("success")
                .message("Dealers fetched successfully")
                .contactNumber(contactNumber)
                .pincode(pincode)
                .category(category)
                .dealers(dealerDtos)
                .bookmarkedDealers(bookmarkedDealerDtos)
                .shopkeeperOrders(orderSummaries)
                .build();
    }

    /**
     * GET /api/v1/dealers/{dealer_id}/products
     */
    public DealerProductPageResponseDto getDealerProducts(Long dealerId, int page, int limit, String sort, String order) {
        log.info("Fetching products for dealer id: {}", dealerId);

        User dealer = userRepository.findById(dealerId)
                .orElseThrow(() -> new UserNotFoundException("Dealer not found with id: " + dealerId));

        if (dealer.getRole() != Role.DEALER) {
            throw new ForbiddenException("User is not a dealer");
        }

        // Get DealerProducts for this dealer
        List<DealerProduct> dealerProducts = dealerProductRepository.findByUser_Id(dealerId);

        // Collect all product items
        List<DealerProductItem> allItems = new ArrayList<>();
        for (DealerProduct dp : dealerProducts) {
            allItems.addAll(dealerProductItemRepository.findByDealerProductId(dp.getId()));
        }

        // Sort items
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if ("price".equalsIgnoreCase(sort)) {
            if (direction == Sort.Direction.ASC) {
                allItems.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            } else {
                allItems.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
            }
        }

        // Manual pagination
        int totalRecords = allItems.size();
        int totalPages = (int) Math.ceil((double) totalRecords / limit);
        int offset = (page - 1) * limit;
        List<DealerProductItem> pageItems = allItems.stream()
                .skip(Math.max(offset, 0))
                .limit(limit)
                .collect(Collectors.toList());

        List<DealerProductItemDto> productDtos = pageItems.stream()
                .map(this::toDealerProductItemDto)
                .collect(Collectors.toList());

        return DealerProductPageResponseDto.builder()
                .status("success")
                .message("Products fetched successfully")
                .dealerId("dl_" + dealerId)
                .pagination(DealerProductPageResponseDto.PaginationDto.builder()
                        .page(page)
                        .limit(limit)
                        .totalRecords(totalRecords)
                        .totalPages(totalPages)
                        .build())
                .products(productDtos)
                .build();
    }

    /**
     * POST /api/v1/dealers/{dealer_id}/bookmark
     */
    @Transactional
    public void bookmarkDealer(Long dealerId) {
        User shopkeeper = getAuthenticatedUser();
        User dealer = userRepository.findById(dealerId)
                .orElseThrow(() -> new UserNotFoundException("Dealer not found with id: " + dealerId));

        if (!bookmarkedDealerRepository.existsByShopkeeperAndDealer(shopkeeper, dealer)) {
            BookmarkedDealer bookmark = BookmarkedDealer.builder()
                    .shopkeeper(shopkeeper)
                    .dealer(dealer)
                    .build();
            bookmarkedDealerRepository.save(bookmark);
            log.info("Dealer {} bookmarked by shopkeeper {}", dealerId, shopkeeper.getId());
        }
    }

    /**
     * DELETE /api/v1/dealers/{dealer_id}/bookmark
     */
    @Transactional
    public void removeBookmark(Long dealerId) {
        User shopkeeper = getAuthenticatedUser();
        User dealer = userRepository.findById(dealerId)
                .orElseThrow(() -> new UserNotFoundException("Dealer not found with id: " + dealerId));
        bookmarkedDealerRepository.deleteByShopkeeperAndDealer(shopkeeper, dealer);
        log.info("Dealer {} bookmark removed by shopkeeper {}", dealerId, shopkeeper.getId());
    }

    // ===== Helpers =====

    private DealerInfoDto toDealerInfoDto(User dealer) {
        List<String> subCategories = dealer.getProductTypes() == null ? List.of() :
                dealer.getProductTypes().stream()
                        .map(ProductType::name)
                        .collect(Collectors.toList());
        return DealerInfoDto.builder()
                .dealerId("dl_" + dealer.getId())
                .name(dealer.getShopName() != null ? dealer.getShopName() : dealer.getOwnerName())
                .contactNumber(dealer.getPhoneNumber())
                .subCategory(subCategories)
                .build();
    }

    private DealerProductItemDto toDealerProductItemDto(DealerProductItem item) {
        String stock = item.getQuantity() > 10 ? "In Stock" : (item.getQuantity() > 0 ? "Low Stock" : "Out of Stock");
        return DealerProductItemDto.builder()
                .productId(item.getProductDetails() != null ? item.getProductDetails().getProductId() : null)
                .productImageUrl(item.getProductDetails() != null ? item.getProductDetails().getImageLocation() : null)
                .name(item.getProductDetails() != null ? item.getProductDetails().getProductName() : null)
                .weight(item.getProductDetails() != null && item.getProductDetails().getQuantityType() != null
                        ? item.getProductDetails().getQuantityType().name() : null)
                .brand(item.getProductDetails() != null ? item.getProductDetails().getCompany() : null)
                .category(item.getProductDetails() != null && item.getProductDetails().getProductType() != null
                        ? item.getProductDetails().getProductType().name() : null)
                .price(item.getPrice())
                .currency("INR")
                .stock(stock)
                .build();
    }

    private ShopkeeperOrderSummaryDto toOrderSummary(Order order) {
        List<ShopkeeperOrderSummaryDto.OrderItemSummaryDto> itemSummaries = order.getItems().stream()
                .map(item -> ShopkeeperOrderSummaryDto.OrderItemSummaryDto.builder()
                        .itemName(item.getProductDetails() != null ? item.getProductDetails().getProductName() : null)
                        .itemWeight(item.getProductDetails() != null && item.getProductDetails().getQuantityType() != null
                                ? item.getProductDetails().getQuantityType().name() : null)
                        .quantity(item.getQuantity())
                        .price(item.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        return ShopkeeperOrderSummaryDto.builder()
                .orderId("ORD_" + order.getId())
                .dealerId("dl_" + (order.getDealer() != null ? order.getDealer().getId() : null))
                .status(order.getOrderStatus().name())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .items(itemSummaries)
                .build();
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("Authentication required");
        }
        return userRepository.findByPhoneNumber(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private User getAuthenticatedUserOptional() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return null;
            }
            return userRepository.findByPhoneNumber(auth.getName()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}

