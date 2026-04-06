package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.User;
import com.shreeganesh.enterprises.service.EmailService;
import com.shreeganesh.enterprises.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // ===================== LOGIN PAGE =====================
    @GetMapping("/login")
    public String loginPage(Authentication auth) {

        if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            return "redirect:/";
        }

        if (auth != null && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        }

        return "login";
    }

    // ===================== SIGNUP =====================
    @GetMapping("/signup")
    public String signupForm(Authentication auth, Model model) {

        if (auth != null) {
            return "redirect:/";
        }

        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@ModelAttribute User user) {
        userService.signup(user);
        return "redirect:/login?registered=true";
    }

    // ===================== FORGOT PASSWORD =====================
    @GetMapping("/forgot-password")
    public String forgotForm(Authentication auth, Model model) {

        if (auth != null) return "redirect:/";

        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotSubmit(@RequestParam String email) {

        userService.findByEmail(email).ifPresent(user -> {
            var token = userService.createPasswordResetToken(email);
            String resetLink =
                    "http://localhost:2330/reset-password?token=" + token.getToken();
            emailService.sendPasswordReset(email, resetLink);
        });

        // Always redirect success (prevents email enumeration)
        return "redirect:/forgot-password?sent=true";
    }

    // ===================== RESET PASSWORD (GET) =====================
    @GetMapping("/reset-password")
    public String resetForm(@RequestParam String token, Model model) {

        var opt = userService.findToken(token);

        if (opt.isEmpty()) {
            model.addAttribute("error", "Invalid password reset link");
            return "forgot-password";
        }

        if (opt.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Reset link has expired. Please request again.");
            return "forgot-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // ===================== RESET PASSWORD (POST) =====================
    @PostMapping("/reset-password")
    public String resetSubmit(@RequestParam String token,
                              @RequestParam String password,
                              @RequestParam String confirmPassword,
                              Model model) {

        var opt = userService.findToken(token);

        if (opt.isEmpty() || opt.get().getExpiresAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Invalid or expired reset link");
            return "forgot-password";
        }

        // Confirm password check
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            model.addAttribute("token", token);
            return "reset-password";
        }

        // Password strength check
        if (!isStrongPassword(password)) {
            model.addAttribute(
                    "error",
                    "Password must be at least 8 characters with uppercase, number and special character"
            );
            model.addAttribute("token", token);
            return "reset-password";
        }

        // Update password
        String email = opt.get().getEmail();
        userService.updatePassword(email, password);

        // 🔒 Prevent reuse of reset link
        userService.invalidateToken(token);

        return "redirect:/login?reset=true";
    }

    // ===================== PASSWORD STRENGTH CHECK =====================
    private boolean isStrongPassword(String password) {
        return password.matches(
                "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$"
        );
    }
}
