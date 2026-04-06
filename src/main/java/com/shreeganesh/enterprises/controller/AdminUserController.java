package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // ================= USERS LIST =================
    @GetMapping
    public String users(Model model) {

        model.addAttribute("users", adminUserService.getAllUsers());
        model.addAttribute("content", "admin/users");

        return "admin/layout";
    }

    // ================= BLOCK USER =================
    @GetMapping("/block/{id}")
    public String block(@PathVariable Long id) {
        adminUserService.blockUser(id);
        return "redirect:/admin/users";
    }

    // ================= UNBLOCK USER =================
    @GetMapping("/unblock/{id}")
    public String unblock(@PathVariable Long id) {
        adminUserService.unblockUser(id);
        return "redirect:/admin/users";
    }
}
