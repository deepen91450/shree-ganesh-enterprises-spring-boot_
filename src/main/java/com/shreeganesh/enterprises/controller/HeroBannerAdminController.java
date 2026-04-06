package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.HeroBanner;
import com.shreeganesh.enterprises.service.HeroBannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/admin/banners")
public class HeroBannerAdminController {

    private static final String UPLOAD_DIR = "F:/enterprises/uploads/hero/";

    @Autowired
    private HeroBannerService service;

    // ================= LIST / MANAGE BANNERS =================
    @GetMapping
    public String manageHero(Model model) {

        model.addAttribute("banners", service.getActiveBanners());
        model.addAttribute("content", "admin/banners");

        return "admin/layout";
    }

    // ================= ADD BANNER =================
    @PostMapping("/add")
    public String addBanner(@RequestParam("file") MultipartFile file,
                            @RequestParam String title,
                            @RequestParam String subtitle) throws IOException {

        if (!file.isEmpty()) {

            String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(
                    file.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            HeroBanner banner = new HeroBanner();
            banner.setImageUrl("/uploads/hero/" + fileName);
            banner.setTitle(title);
            banner.setSubtitle(subtitle);

            service.save(banner);
        }

        return "redirect:/admin/banners";
    }

    // ================= DELETE BANNER =================
    @GetMapping("/delete/{id}")
    public String deleteBanner(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/banners";
    }
}
