package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.service.NotificationService;
import org.springframework.ui.Model;

import com.shreeganesh.enterprises.entity.Enquiry;
import com.shreeganesh.enterprises.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class EnquiryController {

    @org.springframework.beans.factory.annotation.Value("${turnstile.secret}")
    private String turnstileSecret;

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/submit-enquiry")
    public String submitEnquiry(
            @Valid @ModelAttribute Enquiry enquiry,
            BindingResult result,
            RedirectAttributes ra,
            Model model,
            @RequestParam(name = "cf-turnstile-response", required = false) String captcha
    ) {

        // ❌ Validation errors (unchanged)
        if (result.hasErrors()) {
            model.addAttribute("enquiry", enquiry);
            return "contact";
        }

        // ✅ NEW: Check captcha present
        if (captcha == null || captcha.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Please complete captcha.");
            return "redirect:/contact";
        }

        // 🔒 CAPTCHA VALIDATION
        if (!verifyCaptcha(captcha)) {
            ra.addFlashAttribute("errorMsg", "Captcha verification failed. Please try again.");
            return "redirect:/contact";
        }

        // 🔔 Notification (unchanged)
        notificationService.create(
                "New customer enquiry from " + enquiry.getName()
        );

        // ✅ Save enquiry (unchanged)
        enquiryRepository.save(enquiry);

        ra.addFlashAttribute(
                "successMsg",
                "Thank you! Your enquiry has been sent successfully."
        );

        return "redirect:/contact";
    }

    // 🔐 TURNSTILE VERIFICATION METHOD (UPDATED)
    private boolean verifyCaptcha(String captchaResponse) {
        try {
            if (captchaResponse == null || captchaResponse.isEmpty()) {
                return false;
            }

            // ✅ FIX: use injected secret (not hardcoded)
            String secret = turnstileSecret;

            java.net.URL url = new java.net.URL(
                    "https://challenges.cloudflare.com/turnstile/v0/siteverify"
            );

            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            String params = "secret=" + URLEncoder.encode(secret, StandardCharsets.UTF_8)
                    + "&response=" + URLEncoder.encode(captchaResponse, StandardCharsets.UTF_8);

            java.io.OutputStream os = conn.getOutputStream();
            os.write(params.getBytes());
            os.flush();

            java.io.InputStream is = conn.getInputStream();
            String result = new String(is.readAllBytes());

            // 🔥 DEBUG (keep for now)
            

            return result.contains("\"success\":true");

        } catch (Exception e) {
            return false;
        }
    }
}
