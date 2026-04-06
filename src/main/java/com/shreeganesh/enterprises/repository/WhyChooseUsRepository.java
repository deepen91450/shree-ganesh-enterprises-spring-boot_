package com.shreeganesh.enterprises.repository;

import com.shreeganesh.enterprises.entity.WhyChooseUs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WhyChooseUsRepository
        extends JpaRepository<WhyChooseUs, Long> {

    List<WhyChooseUs> findByActiveTrue();
}
