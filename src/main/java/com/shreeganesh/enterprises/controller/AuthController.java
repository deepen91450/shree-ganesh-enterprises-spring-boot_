package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.User;
import com.shreeganesh.enterprises.service.EmailService;
import com.shreeganesh.enterprises.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Value("${app.base-url}")
    private String appBaseUrl;

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
    public String signupSubmit(@ModelAttribute User user, Model model) {
        try {
            userService.signup(user);
            return "redirect:/login?registered=true";
        } catch (IllegalArgumentException e) {
            if ("EMAIL_EXISTS".equals(e.getMessage())) {
                model.addAttribute("error", "An account with this email already exists.");
                model.addAttribute("user", user); // re-populate form fields
            } else {
                model.addAttribute("error", "Something went wrong. Please try again.");
                model.addAttribute("user", user);
            }
            return "signup"; // stay on signup page
        }
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
            String resetLink = UriComponentsBuilder
                    .fromHttpUrl(appBaseUrl)
                    .path("/reset-password")
                    .queryParam("token", token.getToken())
                    .toUriString();
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

    // ===================== ENTER PHONE =====================
    @GetMapping("/enter-phone")
    public String showPhonePage() {
        return "enter-phone";
    }

    // ===================== SAVE PHONE =====================
    @PostMapping("/save-phone")
    public String savePhone(@RequestParam String phone,
                            Authentication authentication,
                            Model model) {

        // 🔐 Validate phone
        if (!phone.matches("^[6-9]\\d{9}$")) {
            model.addAttribute("error", "Invalid phone number");
            return "enter-phone";
        }

        var oauthUser = (org.springframework.security.oauth2.core.user.OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");

        userService.findByEmail(email).ifPresent(user -> {
            user.setPhone(phone);
            userService.save(user);
        });

        return "redirect:/";
    }
}
