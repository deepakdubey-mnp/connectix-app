package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Order;
import com.example.usermanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByShopkeeper(User shopkeeper, Pageable pageable);

    Optional<Order> findByIdAndShopkeeper(Long id, User shopkeeper);
}

