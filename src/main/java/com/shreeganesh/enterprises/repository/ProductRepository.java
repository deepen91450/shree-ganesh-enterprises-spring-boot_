package com.shreeganesh.enterprises.repository;

import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.entity.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryId(Long categoryId);


    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(String keyword);

    @Query("""
    SELECT p FROM Product p
    WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))
       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%'))
""")
    List<Product> search(@Param("q") String q);


    @Query("""
    SELECT p FROM Product p
    WHERE p.stockQuantity > 0
      AND p.stockQuantity <= :threshold
    ORDER BY p.stockQuantity ASC
    """)
    List<Product> findLowStockProducts(
            @Param("threshold") int threshold,
            Pageable pageable
    );

    long countByStatus(ProductStatus status);

    // ✅ NEW (for pagination + search)
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);



}
