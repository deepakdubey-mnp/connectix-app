package com.example.usermanagement.repository;

import com.example.usermanagement.entity.ProductType;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByRole(Role role);

    @Query("SELECT DISTINCT u FROM User u JOIN u.productTypes pt WHERE u.role = :role AND (:pincode IS NULL OR u.pinCode = :pincode) AND (:productType IS NULL OR pt = :productType)")
    List<User> findDealersByPincodeAndCategory(
            @Param("role") Role role,
            @Param("pincode") Integer pincode,
            @Param("productType") ProductType productType);
}
