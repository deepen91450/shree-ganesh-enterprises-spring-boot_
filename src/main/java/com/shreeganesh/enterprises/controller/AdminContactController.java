package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.ContactSettings;
import com.shreeganesh.enterprises.repository.ContactSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/contact")
public class AdminContactController {

    private static final Long CONTACT_SETTINGS_ID = 1L;

    @Autowired
    private ContactSettingsRepository repo;

    /**
     * Show Contact Settings page in Admin Dashboard
     */
    @GetMapping
    public String showContactSettings(Model model) {

        ContactSettings contact = repo.findById(CONTACT_SETTINGS_ID)
                .orElseGet(() -> {
                    ContactSettings cs = new ContactSettings();
                    cs.setId(CONTACT_SETTINGS_ID);
                    return cs;
                });

        model.addAttribute("contact", contact);
        model.addAttribute("content", "admin/contact-settings");

        return "admin/layout";
    }

    /**
     * Save / Update Contact Settings
     */
    @PostMapping("/save")
    public String saveContactSettings(
            @ModelAttribute("contact") ContactSettings contact) {

        // Force single-row configuration
        contact.setId(CONTACT_SETTINGS_ID);

        repo.save(contact);

        return "redirect:/admin/contact?success";
    }
}
