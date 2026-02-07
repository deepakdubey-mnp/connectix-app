package com.example.usermanagement.repository;

import com.example.usermanagement.entity.ProductDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductDetailsRepository extends JpaRepository<ProductDetails, Long> {

    Optional<ProductDetails> findByProductNameAndCompany(String productName, String company);

    Optional<ProductDetails> findTopByOrderByIdDesc();
}
