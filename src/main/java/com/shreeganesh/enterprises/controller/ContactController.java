package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.ContactSettings;
import com.shreeganesh.enterprises.repository.ContactSettingsRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactController {

    private final ContactSettingsRepository repo;

    public ContactController(ContactSettingsRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {

        ContactSettings contact = repo.findById(1L).orElse(null);
        model.addAttribute("contact", contact);

        // ✅ FULL PAGE (no layout system)
        return "user/contact";
    }
}
