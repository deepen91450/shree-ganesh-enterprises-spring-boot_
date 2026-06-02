package com.shreeganesh.enterprises.repository;

import com.shreeganesh.enterprises.entity.Enquiry;
import com.shreeganesh.enterprises.entity.ProductEnquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {

    long countByAdminReplyNull();

    List<Enquiry> findByUserEmail(String userEmail);

    @Query("""
        SELECT e FROM Enquiry e
        WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :q, '%'))
           OR LOWER(e.email) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<Enquiry> searchCustomerEnquiries(@Param("q") String q);

    Page<Enquiry> findAllByOrderByCreatedAtDesc(Pageable pageable);
}



