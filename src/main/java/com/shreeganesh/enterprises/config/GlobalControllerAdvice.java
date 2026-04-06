package com.shreeganesh.enterprises.config;

import com.shreeganesh.enterprises.entity.SiteSettings;
import com.shreeganesh.enterprises.repository.SiteSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private SiteSettingsRepository repo;

    @ModelAttribute("siteSettings")
    public SiteSettings siteSettings() {
        return repo.findAll().stream().findFirst().orElse(null);
    }
}
