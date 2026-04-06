package com.shreeganesh.enterprises.repository;


import com.shreeganesh.enterprises.entity.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByUserEmail(String email);

    CartItemEntity findByUserEmailAndProductId(String email, Long productId);

    void deleteByUserEmail(String email);

    void deleteByUserEmailAndProductId(String email, Long productId);
}
