package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.WhyChooseUs;
import com.shreeganesh.enterprises.service.WhyChooseUsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/admin/why-choose-us")
public class AdminWhyChooseUsController {

    private final WhyChooseUsService service;

    public AdminWhyChooseUsController(WhyChooseUsService service) {
        this.service = service;
    }

    /* ================= LIST PAGE ================= */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", service.getAll());

        // ✅ fragment-based loading (VERY IMPORTANT)
        model.addAttribute("content", "admin/why-choose-us");

        return "admin/layout";
    }

    /* ================= SAVE WITH IMAGE UPLOAD ================= */
    @PostMapping("/save")
    public String save(@RequestParam("title") String title,
                       @RequestParam("description") String description,
                       @RequestParam("iconFile") MultipartFile iconFile) {

        try {
            // upload directory
            String uploadDir = "uploads/why-choose-us/";
            Files.createDirectories(Paths.get(uploadDir));

            // unique file name
            String fileName = UUID.randomUUID() + "_" + iconFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            // save file
            Files.write(filePath, iconFile.getBytes());

            // save DB record
            WhyChooseUs item = new WhyChooseUs();
            item.setTitle(title);
            item.setDescription(description);
            item.setIconPath("/uploads/why-choose-us/" + fileName);
            item.setActive(true);

            service.save(item);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/why-choose-us";
    }

    /* ================= DELETE ================= */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/why-choose-us";
    }
}
