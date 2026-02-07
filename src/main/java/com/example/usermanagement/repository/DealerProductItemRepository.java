package com.example.usermanagement.repository;

import com.example.usermanagement.entity.DealerProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealerProductItemRepository extends JpaRepository<DealerProductItem, Long> {

    List<DealerProductItem> findByDealerProductId(Long dealerProductId);
}
