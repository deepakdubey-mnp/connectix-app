package com.example.usermanagement.service;

import com.example.usermanagement.dto.ShopDetailsRequestDto;
import com.example.usermanagement.dto.ShopkeeperResponseDto;
import com.example.usermanagement.entity.ShopDetails;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.ForbiddenException;
import com.example.usermanagement.exception.ShopDetailsNotFoundException;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.ShopDetailsRepository;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopDetailsService {

    private final ShopDetailsRepository shopDetailsRepository;
    private final UserRepository userRepository;

    public ShopDetails saveShopDetails(ShopDetailsRequestDto dto) {
        log.debug("Saving new shop details: {}", dto);
        ShopDetails shopDetails = new ShopDetails();
        shopDetails.setOwnerName(dto.getOwnerName());
        shopDetails.setShopName(dto.getShopName());
        shopDetails.setCategory(dto.getCategory());
        shopDetails.setGstNumber(dto.getGstNumber());
        shopDetails.setAddress(dto.getAddress());
        shopDetails.setPincode(dto.getPincode());
        shopDetails.setActive(dto.getActive() != null ? dto.getActive() : true);
        ShopDetails saved = shopDetailsRepository.save(shopDetails);
        log.info("Shop details saved with id: {}", saved.getId());
        return saved;
    }

    public ShopDetails updateShopDetails(Long id, ShopDetailsRequestDto dto) {
        log.debug("Updating shop details for id {}: {}", id, dto);
        ShopDetails shopDetails = shopDetailsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ShopDetails not found with id: {}", id);
                    return new ShopDetailsNotFoundException("Shop details not found with id: " + id);
                });
        shopDetails.setOwnerName(dto.getOwnerName());
        shopDetails.setShopName(dto.getShopName());
        shopDetails.setCategory(dto.getCategory());
        shopDetails.setGstNumber(dto.getGstNumber());
        shopDetails.setAddress(dto.getAddress());
        shopDetails.setPincode(dto.getPincode());
        if (dto.getActive() != null) {
            shopDetails.setActive(dto.getActive());
        }
        ShopDetails updated = shopDetailsRepository.save(shopDetails);
        log.info("Shop details updated for id: {}", id);
        return updated;
    }

    public void deleteShopDetails(Long id) {
        log.debug("Deleting shop details for id: {}", id);
        if (!shopDetailsRepository.existsById(id)) {
            log.error("ShopDetails not found with id: {}", id);
            throw new ShopDetailsNotFoundException("Shop details not found with id: " + id);
        }
        shopDetailsRepository.deleteById(id);
        log.info("Shop details deleted for id: {}", id);
    }

    public ShopDetails getShopDetailsById(Long id) {
        log.debug("Fetching shop details for id: {}", id);
        return shopDetailsRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ShopDetails not found with id: {}", id);
                    return new ShopDetailsNotFoundException("Shop details not found with id: " + id);
                });
    }

    public List<ShopDetails> getAllShopDetails() {
        log.debug("Fetching all shop details");
        return shopDetailsRepository.findAll();
    }

    // ===== V1 Shopkeeper Methods =====

    /**
     * Creates or updates shopkeeper info for the authenticated user.
     */
    public ShopkeeperResponseDto saveShopkeeperV1(ShopDetailsRequestDto dto) {
        User currentUser = getAuthenticatedUser();
        log.info("Saving shopkeeper info for user id: {}", currentUser.getId());

        // Upsert: if shopkeeper already registered, update; otherwise create new
        ShopDetails shopDetails = shopDetailsRepository.findByUser_Id(currentUser.getId())
                .orElse(new ShopDetails());

        shopDetails.setUser(currentUser);
        shopDetails.setOwnerName(dto.getOwnerName());
        shopDetails.setShopName(dto.getShopName());
        shopDetails.setCategory(dto.getCategory());
        shopDetails.setGstNumber(dto.getGstNumber());
        shopDetails.setAddress(dto.getAddress());
        shopDetails.setPincode(dto.getPincode());
        shopDetails.setActive(dto.getActive() != null ? dto.getActive() : true);

        ShopDetails saved = shopDetailsRepository.save(shopDetails);
        log.info("Shopkeeper info saved with id: {}", saved.getId());

        return buildShopkeeperResponse("Shopkeeper information stored successfully", saved);
    }

    /**
     * Updates shopkeeper info by shopkeeper_id for authenticated user.
     */
    public ShopkeeperResponseDto updateShopkeeperV1(Long shopkeeperId, ShopDetailsRequestDto dto) {
        User currentUser = getAuthenticatedUser();
        log.info("Updating shopkeeper info id: {} for user id: {}", shopkeeperId, currentUser.getId());

        ShopDetails shopDetails = shopDetailsRepository.findById(shopkeeperId)
                .orElseThrow(() -> new ShopDetailsNotFoundException("Shopkeeper not found with id: " + shopkeeperId));

        // Ensure the shopkeeper belongs to the current user
        if (shopDetails.getUser() == null || !shopDetails.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You do not have permission to update this shopkeeper");
        }

        shopDetails.setOwnerName(dto.getOwnerName());
        shopDetails.setShopName(dto.getShopName());
        shopDetails.setCategory(dto.getCategory());
        shopDetails.setGstNumber(dto.getGstNumber());
        shopDetails.setAddress(dto.getAddress());
        shopDetails.setPincode(dto.getPincode());
        if (dto.getActive() != null) {
            shopDetails.setActive(dto.getActive());
        }

        ShopDetails updated = shopDetailsRepository.save(shopDetails);
        log.info("Shopkeeper info updated for id: {}", shopkeeperId);

        return buildShopkeeperResponse("Shopkeeper information updated successfully", updated);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ForbiddenException("Authentication required");
        }
        String phoneNumber = auth.getName();
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private ShopkeeperResponseDto buildShopkeeperResponse(String message, ShopDetails shopDetails) {
        return ShopkeeperResponseDto.builder()
                .status("success")
                .message(message)
                .shopkeeperId("sk_" + shopDetails.getId())
                .data(ShopkeeperResponseDto.ShopkeeperDataDto.builder()
                        .ownerName(shopDetails.getOwnerName())
                        .shopName(shopDetails.getShopName())
                        .category(shopDetails.getCategory())
                        .gstNumber(shopDetails.getGstNumber())
                        .address(shopDetails.getAddress())
                        .pincode(shopDetails.getPincode())
                        .build())
                .build();
    }
}
