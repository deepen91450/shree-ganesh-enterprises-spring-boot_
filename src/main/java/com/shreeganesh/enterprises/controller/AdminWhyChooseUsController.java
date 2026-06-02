package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.WhyChooseUs;
import com.shreeganesh.enterprises.service.UploadStorageService;
import com.shreeganesh.enterprises.service.WhyChooseUsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/why-choose-us")
public class AdminWhyChooseUsController {

    private final WhyChooseUsService service;
    private final UploadStorageService uploadStorageService;

    public AdminWhyChooseUsController(WhyChooseUsService service,
                                      UploadStorageService uploadStorageService) {
        this.service = service;
        this.uploadStorageService = uploadStorageService;
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
            // save DB record
            WhyChooseUs item = new WhyChooseUs();
            item.setTitle(title);
            item.setDescription(description);
            item.setIconPath(uploadStorageService.storeImage(iconFile, "why-choose-us"));
            item.setActive(true);

            service.save(item);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/why-choose-us";
    }

    /* ================= DELETE ================= */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/admin/why-choose-us";
    }
}
