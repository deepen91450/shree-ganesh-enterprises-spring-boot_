package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.AboutPage;
import com.shreeganesh.enterprises.service.AboutPageService;
import com.shreeganesh.enterprises.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @Autowired
    private AboutPageService aboutPageService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/about")
    public String about(Model model) {

        AboutPage about = aboutPageService.getAboutPage();

        model.addAttribute("about", about);
        model.addAttribute("categories", categoryService.getRootCategories());

        // ✅ FULL PAGE (no layout system)
        return "user/about";
    }
}
