package com.shreeganesh.enterprises.repository;

import com.shreeganesh.enterprises.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByUserEmail(String email);
}
