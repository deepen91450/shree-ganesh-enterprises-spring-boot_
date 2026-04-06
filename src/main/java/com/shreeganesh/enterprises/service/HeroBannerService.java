package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.HeroBanner;
import com.shreeganesh.enterprises.repository.HeroBannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeroBannerService {

    @Autowired
    private HeroBannerRepository repo;

    public List<HeroBanner> getActiveBanners() {
        return repo.findByActiveTrue();
    }

    public void save(HeroBanner banner) {
        repo.save(banner);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}

