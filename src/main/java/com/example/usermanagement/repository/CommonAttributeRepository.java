package com.example.usermanagement.repository;

import com.example.usermanagement.entity.CommonAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommonAttributeRepository extends JpaRepository<CommonAttribute, Long> {

    Optional<CommonAttribute> findByKeyAndLanguage(String key, String language);

    Optional<CommonAttribute> findTopByOrderByIdDesc();
}
