package com.shreeganesh.enterprises.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

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
    public String about(Model model, HttpServletRequest request) {

        // Resolve CSRF token before Thymeleaf starts rendering the page
        CsrfToken csrfToken =
                (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            csrfToken.getToken();
        }

        AboutPage about = aboutPageService.getAboutPage();

        model.addAttribute("about", about);
        model.addAttribute("categories", categoryService.getRootCategories());

        return "user/about";
    }
}
