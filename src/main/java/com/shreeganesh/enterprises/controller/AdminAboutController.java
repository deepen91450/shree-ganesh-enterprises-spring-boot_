package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.AboutPage;
import com.shreeganesh.enterprises.repository.AboutPageRepository;
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
@RequestMapping("/admin/about")
public class AdminAboutController {

    @Autowired
    private AboutPageRepository aboutRepo;

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

        // Upload folder
        String uploadDir = "uploads/about/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        // If new image uploaded
        if (aboutImageFile != null && !aboutImageFile.isEmpty()) {

            String fileName = System.currentTimeMillis() + "_" +
                    aboutImageFile.getOriginalFilename();

            Path filePath = Paths.get(uploadDir + fileName);
            Files.copy(
                    aboutImageFile.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Save image path in DB
            about.setAboutImageUrl("/uploads/about/" + fileName);
        }

        aboutRepo.save(about);
        return "redirect:/admin/about";
    }

}
