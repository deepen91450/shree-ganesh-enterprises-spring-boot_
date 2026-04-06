package com.shreeganesh.enterprises.repository;

import com.shreeganesh.enterprises.entity.SiteSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSettingsRepository extends JpaRepository<SiteSettings, Long> {
}
