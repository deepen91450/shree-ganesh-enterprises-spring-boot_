package com.shreeganesh.enterprises.repository;


import com.shreeganesh.enterprises.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);

    @Query("""
    SELECT c FROM Category c
    WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :q, '%'))
""")
    List<Category> search(@Param("q") String q);

}

