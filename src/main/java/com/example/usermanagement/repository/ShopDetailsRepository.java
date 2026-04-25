package com.example.usermanagement.repository;

import com.example.usermanagement.entity.ShopDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopDetailsRepository extends JpaRepository<ShopDetails, Long> {
}

