package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.AboutPage;
import com.shreeganesh.enterprises.repository.AboutPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AboutPageService {

    @Autowired
    private AboutPageRepository aboutRepo;

    public AboutPage getAboutPage() {
        return aboutRepo.findAll()
                .stream()
                .findFirst()
                .orElse(null);
    }
}
