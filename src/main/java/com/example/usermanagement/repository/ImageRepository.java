package com.example.usermanagement.repository;

import com.example.usermanagement.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Image entity.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
}