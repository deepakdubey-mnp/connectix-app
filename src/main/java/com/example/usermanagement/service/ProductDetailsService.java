package com.example.usermanagement.service;

import com.example.usermanagement.dto.ProductDetailsRequestDto;
import com.example.usermanagement.entity.ProductDetails;
import com.example.usermanagement.exception.ProductAlreadyExistsException;
import com.example.usermanagement.exception.ProductNotFoundException;
import com.example.usermanagement.repository.ProductDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductDetailsService {

    private final ProductDetailsRepository productDetailsRepository;

    @Transactional
    public ProductDetails add(ProductDetailsRequestDto dto) {
        log.info("Adding product: {} for company: {}", dto.getProductName(), dto.getCompany());

        if (productDetailsRepository.findByProductNameAndCompany(dto.getProductName(), dto.getCompany()).isPresent()) {
            throw new ProductAlreadyExistsException(
                    "Product with name '" + dto.getProductName() + "' already exists for company '" + dto.getCompany() + "'");
        }

        Long newId = productDetailsRepository.findTopByOrderByIdDesc()
                .map(p -> p.getId() + 1)
                .orElse(1L);

        ProductDetails product = mapToEntity(dto);
        product.setId(newId);
        ProductDetails saved = productDetailsRepository.save(product);
        log.info("Product added with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public ProductDetails update(Long id, ProductDetailsRequestDto dto) {
        log.info("Updating product with id: {}", id);

        ProductDetails existing = productDetailsRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        productDetailsRepository.findByProductNameAndCompany(dto.getProductName(), dto.getCompany())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> {
                    throw new ProductAlreadyExistsException(
                            "Product with name '" + dto.getProductName() + "' already exists for company '" + dto.getCompany() + "'");
                });

        existing.setProductId(dto.getProductId());
        existing.setProductName(dto.getProductName());
        existing.setProductDescription(dto.getProductDescription());
        existing.setCompany(dto.getCompany());
        existing.setProductType(dto.getProductType());
        existing.setProductSubType(dto.getProductSubType());
        existing.setImageLocation(dto.getImageLocation());
        existing.setQuantityType(dto.getQuantityType());

        ProductDetails updated = productDetailsRepository.save(existing);
        log.info("Product updated with id: {}", updated.getId());
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting product with id: {}", id);

        if (!productDetailsRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productDetailsRepository.deleteById(id);
        log.info("Product deleted with id: {}", id);
    }

    public ProductDetails getById(Long id) {
        return productDetailsRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public List<ProductDetails> getAll() {
        return productDetailsRepository.findAll();
    }

    private ProductDetails mapToEntity(ProductDetailsRequestDto dto) {
        return ProductDetails.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .productDescription(dto.getProductDescription())
                .company(dto.getCompany())
                .productType(dto.getProductType())
                .productSubType(dto.getProductSubType())
                .imageLocation(dto.getImageLocation())
                .quantityType(dto.getQuantityType())
                .build();
    }
}
