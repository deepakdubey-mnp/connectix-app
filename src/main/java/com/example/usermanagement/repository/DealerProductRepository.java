package com.example.usermanagement.repository;

import com.example.usermanagement.entity.DealerProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealerProductRepository extends JpaRepository<DealerProduct, Long> {

    List<DealerProduct> findByUser_Id(Long userId);

    Optional<DealerProduct> findByIdAndUser_Id(Long id, Long userId);
}
