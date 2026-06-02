package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.HeroBanner;
import com.shreeganesh.enterprises.service.HeroBannerService;
import com.shreeganesh.enterprises.service.UploadStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@Controller
@RequestMapping("/admin/banners")
public class HeroBannerAdminController {

    @Autowired
    private HeroBannerService service;

    @Autowired
    private UploadStorageService uploadStorageService;

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

            HeroBanner banner = new HeroBanner();
            banner.setImageUrl(uploadStorageService.storeImage(file, "hero"));
            banner.setTitle(title);
            banner.setSubtitle(subtitle);

            service.save(banner);
        }

        return "redirect:/admin/banners";
    }

    // ================= DELETE BANNER =================
    @PostMapping("/delete/{id}")
    public String deleteBanner(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/banners";
    }
}
