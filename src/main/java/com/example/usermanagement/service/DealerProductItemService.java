package com.example.usermanagement.service;

import com.example.usermanagement.dto.DealerProductItemRequestDto;
import com.example.usermanagement.dto.DealerProductItemUpdateDto;
import com.example.usermanagement.entity.DealerProduct;
import com.example.usermanagement.entity.DealerProductItem;
import com.example.usermanagement.entity.ProductDetails;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.DealerProductItemNotFoundException;
import com.example.usermanagement.exception.ForbiddenException;
import com.example.usermanagement.exception.ProductNotFoundException;
import com.example.usermanagement.repository.DealerProductItemRepository;
import com.example.usermanagement.repository.DealerProductRepository;
import com.example.usermanagement.repository.ProductDetailsRepository;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DealerProductItemService {

    private final DealerProductItemRepository dealerProductItemRepository;
    private final DealerProductRepository dealerProductRepository;
    private final ProductDetailsRepository productDetailsRepository;
    private final UserRepository userRepository;

    private User getCurrentDealer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new ForbiddenException("Authentication required");
        }
        String phoneNumber = auth.getName();
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ForbiddenException("User not found"));
        if (user.getRole() != Role.DEALER) {
            throw new ForbiddenException("Only dealers can perform this action");
        }
        return user;
    }

    private void ensureDealerOwnsDealerProduct(DealerProduct dealerProduct, User dealer) {
        if (dealerProduct.getUser() == null || !dealerProduct.getUser().getId().equals(dealer.getId())) {
            throw new ForbiddenException("You do not have access to this dealer product");
        }
    }

    @Transactional
    public DealerProductItem add(DealerProductItemRequestDto dto) {
        User dealer = getCurrentDealer();
        log.info("Dealer {} adding product item to dealerProduct {}", dealer.getId(), dto.getDealerProductId());

        DealerProduct dealerProduct = dealerProductRepository.findById(dto.getDealerProductId())
                .orElseThrow(() -> new DealerProductItemNotFoundException("Dealer product not found with id: " + dto.getDealerProductId()));
        ensureDealerOwnsDealerProduct(dealerProduct, dealer);

        ProductDetails productDetails = productDetailsRepository.findById(dto.getProductDetailsId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + dto.getProductDetailsId()));

        DealerProductItem item = DealerProductItem.builder()
                .dealerProduct(dealerProduct)
                .productDetails(productDetails)
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .build();
        DealerProductItem saved = dealerProductItemRepository.save(item);
        log.info("Dealer product item added with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public DealerProductItem updateQuantityAndPrice(Long id, DealerProductItemUpdateDto dto) {
        User dealer = getCurrentDealer();
        log.info("Dealer {} updating product item {}", dealer.getId(), id);

        DealerProductItem item = dealerProductItemRepository.findById(id)
                .orElseThrow(() -> new DealerProductItemNotFoundException("Dealer product item not found with id: " + id));
        ensureDealerOwnsDealerProduct(item.getDealerProduct(), dealer);

        item.setQuantity(dto.getQuantity());
        item.setPrice(dto.getPrice());
        DealerProductItem updated = dealerProductItemRepository.save(item);
        log.info("Dealer product item updated with id: {}", updated.getId());
        return updated;
    }

    public DealerProductItem getById(Long id) {
        User dealer = getCurrentDealer();
        DealerProductItem item = dealerProductItemRepository.findById(id)
                .orElseThrow(() -> new DealerProductItemNotFoundException("Dealer product item not found with id: " + id));
        ensureDealerOwnsDealerProduct(item.getDealerProduct(), dealer);
        return item;
    }

    public List<DealerProductItem> getByDealerProductId(Long dealerProductId) {
        User dealer = getCurrentDealer();
        DealerProduct dealerProduct = dealerProductRepository.findById(dealerProductId)
                .orElseThrow(() -> new DealerProductItemNotFoundException("Dealer product not found with id: " + dealerProductId));
        ensureDealerOwnsDealerProduct(dealerProduct, dealer);
        return dealerProductItemRepository.findByDealerProductId(dealerProductId);
    }

    @Transactional
    public void delete(Long id) {
        User dealer = getCurrentDealer();
        log.info("Dealer {} deleting product item {}", dealer.getId(), id);

        DealerProductItem item = dealerProductItemRepository.findById(id)
                .orElseThrow(() -> new DealerProductItemNotFoundException("Dealer product item not found with id: " + id));
        ensureDealerOwnsDealerProduct(item.getDealerProduct(), dealer);
        dealerProductItemRepository.deleteById(id);
        log.info("Dealer product item deleted with id: {}", id);
    }
}
