package com.shreeganesh.enterprises.repository;

import com.shreeganesh.enterprises.entity.ProductEnquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductEnquiryRepository
        extends JpaRepository<ProductEnquiry, Long> {

    List<ProductEnquiry> findByUserId(Long userId);

    long countByAdminReplyNull();

    @Query("""
        SELECT DISTINCT e FROM ProductEnquiry e
        LEFT JOIN e.items i
        WHERE LOWER(e.userName) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(e.userEmail) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(e.userPhone) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(i.productName) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<ProductEnquiry> searchProductEnquiries(@Param("q") String q);
}
