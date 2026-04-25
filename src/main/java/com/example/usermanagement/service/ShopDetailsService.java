package com.example.usermanagement.service;

import com.example.usermanagement.dto.ShopDetailsRequestDto;
import com.example.usermanagement.entity.ShopDetails;
import com.example.usermanagement.exception.ShopDetailsNotFoundException;
import com.example.usermanagement.repository.ShopDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopDetailsService {

    private final ShopDetailsRepository shopDetailsRepository;

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
}
