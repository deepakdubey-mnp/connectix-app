package com.example.usermanagement.repository;

import com.example.usermanagement.entity.BookmarkedDealer;
import com.example.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkedDealerRepository extends JpaRepository<BookmarkedDealer, Long> {

    List<BookmarkedDealer> findByShopkeeper(User shopkeeper);

    Optional<BookmarkedDealer> findByShopkeeperAndDealer(User shopkeeper, User dealer);

    boolean existsByShopkeeperAndDealer(User shopkeeper, User dealer);

    void deleteByShopkeeperAndDealer(User shopkeeper, User dealer);
}

