package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Notification;
import com.example.usermanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndReadOrderByCreatedAtDesc(User user, boolean read);
    Optional<Notification> findByIdAndUser(Long id, User user);
    long countByUserAndRead(User user, boolean read);
}
