package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.AboutPage;
import com.shreeganesh.enterprises.repository.AboutPageRepository;
import com.shreeganesh.enterprises.service.UploadStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
@Controller
@RequestMapping("/admin/about")
public class AdminAboutController {

    @Autowired
    private AboutPageRepository aboutRepo;

    @Autowired
    private UploadStorageService uploadStorageService;

    // 🔹 SHOW ABOUT PAGE EDIT FORM
    @GetMapping
    public String editAbout(Model model) {

        // Ensure only ONE record exists
        AboutPage about = aboutRepo.findAll()
                .stream()
                .findFirst()
                .orElse(new AboutPage());

        model.addAttribute("about", about);
        model.addAttribute("content", "admin/about");
        return "admin/layout";
    }

    // 🔹 SAVE / UPDATE ABOUT PAGE
    @PostMapping("/save")
    public String saveAbout(
            @ModelAttribute AboutPage about,
            @RequestParam("aboutImageFile") MultipartFile aboutImageFile
    ) throws Exception {

        // If new image uploaded
        if (aboutImageFile != null && !aboutImageFile.isEmpty()) {
            uploadStorageService.deletePublicFile(about.getAboutImageUrl());
            about.setAboutImageUrl(uploadStorageService.storeImage(aboutImageFile, "about"));
        }

        aboutRepo.save(about);
        return "redirect:/admin/about";
    }

}
