package com.shreeganesh.enterprises.repository;

import com.shreeganesh.enterprises.entity.OrderTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderTable, Long> {
    List<OrderTable> findByUserEmail(String email);
}
