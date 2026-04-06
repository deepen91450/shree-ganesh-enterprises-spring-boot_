package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.SiteSettings;
import com.shreeganesh.enterprises.repository.SiteSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin/site-settings")
public class AdminSiteSettingsController {

    @Autowired
    private SiteSettingsRepository repo;

    // ===================== PAGE LOAD =====================
    @GetMapping
    public String edit(Model model) {
        SiteSettings settings = repo.findAll().stream()
                .findFirst()
                .orElse(new SiteSettings());

        model.addAttribute("settings", settings);
        model.addAttribute("content", "admin/site-settings");
        return "admin/layout";
    }

    // ===================== SAVE SETTINGS =====================
    @PostMapping("/save")
    public String save(
            // -------- HEADER --------
            @RequestParam(value = "siteTitle", required = false) String siteTitle,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,

            // -------- FOOTER --------
            @RequestParam(value = "footerCompanyName", required = false) String footerCompanyName,
            @RequestParam(value = "footerDescription", required = false) String footerDescription,
            @RequestParam(value = "footerAddress", required = false) String footerAddress,
            @RequestParam(value = "footerPhone", required = false) String footerPhone,
            @RequestParam(value = "footerEmail", required = false) String footerEmail,

            // -------- SOCIAL LINKS --------
            @RequestParam(value = "whatsappUrl", required = false) String whatsappUrl,
            @RequestParam(value = "instagramUrl", required = false) String instagramUrl,
            @RequestParam(value = "facebookUrl", required = false) String facebookUrl,
            @RequestParam(value = "linkedinUrl", required = false) String linkedinUrl,

            // -------- COPYRIGHT --------
            @RequestParam(value = "footerCopyright", required = false) String footerCopyright
    ) throws Exception {

        // 1️⃣ Load existing settings OR create new
        SiteSettings settings = repo.findAll()
                .stream()
                .findFirst()
                .orElse(new SiteSettings());

        // ================= HEADER =================
        if (siteTitle != null && !siteTitle.isBlank()) {
            settings.setSiteTitle(siteTitle);
        }

        if (logoFile != null && !logoFile.isEmpty()) {
            String uploadDir = "uploads/logo/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + logoFile.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);

            Files.copy(logoFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            settings.setLogoPath("/uploads/logo/" + fileName);
        }

        // ================= FOOTER =================
        settings.setFooterCompanyName(footerCompanyName);
        settings.setFooterDescription(footerDescription);
        settings.setFooterAddress(footerAddress);
        settings.setFooterPhone(footerPhone);
        settings.setFooterEmail(footerEmail);

        // ================= SOCIAL LINKS =================
        settings.setWhatsappUrl(whatsappUrl);
        settings.setInstagramUrl(instagramUrl);
        settings.setFacebookUrl(facebookUrl);
        settings.setLinkedinUrl(linkedinUrl);

        // ================= COPYRIGHT =================
        settings.setFooterCopyright(footerCopyright);

        // 4️⃣ Save
        repo.save(settings);

        return "redirect:/admin/site-settings";
    }
}
