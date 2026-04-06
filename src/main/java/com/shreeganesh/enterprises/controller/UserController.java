package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.User;
import com.shreeganesh.enterprises.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ▶ Load Profile Page
    @GetMapping("/profile")
    public String profilePage(Authentication auth, Model model) {

        if (auth == null) return "redirect:/login";

        String email = auth.getName();

        User user = userService.findByEmail(email).orElse(null);

        if (user == null) return "redirect:/login";

        model.addAttribute("user", user);

        return "profile";  // loads profile.html
    }

    // ▶ Update user profile
    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User formUser, Authentication auth) {

        String email = auth.getName();

        userService.updateProfile(email, formUser);

        return "redirect:/";
    }
}
