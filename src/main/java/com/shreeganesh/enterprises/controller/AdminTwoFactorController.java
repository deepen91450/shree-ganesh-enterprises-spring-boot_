package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.Admin;
import com.shreeganesh.enterprises.repository.AdminRepository;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@Controller
@RequestMapping("/admin/2fa")
public class AdminTwoFactorController {

    @Autowired
    private AdminRepository adminRepository;

    /* -------------------------------------------------
       ENTRY POINT
    ------------------------------------------------- */
    @GetMapping
    public String twoFactorHome(Authentication auth,
                                HttpServletRequest request) {

        String email = auth.getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow();

        Boolean verified =
                (Boolean) request.getSession()
                        .getAttribute("ADMIN_2FA_VERIFIED");

        // If already verified for this session → dashboard
        if (Boolean.TRUE.equals(verified)) {
            return "redirect:/admin/dashboard";
        }

        // If 2FA configured → show OTP page (NO QR)
        if (admin.isTwoFactorEnabled()) {
            return "otp";
        }

        // First time → setup QR
        return "redirect:/admin/2fa/setup";
    }

    /* -------------------------------------------------
       SETUP QR CODE (ONLY ONCE)
    ------------------------------------------------- */
    @GetMapping("/setup")
    public String setup2FA(Authentication auth, Model model) {

        String email = auth.getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow();

        // Already configured → go to OTP
        if (admin.isTwoFactorEnabled()) {
            return "redirect:/admin/2fa";
        }

        if (admin.getTwoFactorSecret() == null) {
            SecretGenerator secretGenerator = new DefaultSecretGenerator();
            admin.setTwoFactorSecret(secretGenerator.generate());
            adminRepository.save(admin);
        }

        QrData data = new QrData.Builder()
                .label(admin.getEmail())
                .secret(admin.getTwoFactorSecret())
                .issuer("Shree Ganesh Enterprises Admin")
                .build();

        QrGenerator generator = new ZxingPngQrGenerator();

        try {
            String qrImage = Base64.getEncoder()
                    .encodeToString(generator.generate(data));
            model.addAttribute("qr", qrImage);
        } catch (Exception e) {
            throw new RuntimeException("QR generation failed", e);
        }

        return "otp";
    }

    /* -------------------------------------------------
       VERIFY OTP
    ------------------------------------------------- */
    @PostMapping("/verify")
    public String verifyOtp(@RequestParam String otp,
                            Authentication auth,
                            HttpServletRequest request,
                            Model model) {

        String email = auth.getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow();

        CodeVerifier verifier = new DefaultCodeVerifier(
                new DefaultCodeGenerator(),
                new SystemTimeProvider()
        );

        boolean valid = verifier.isValidCode(
                admin.getTwoFactorSecret(),
                otp
        );

        if (!valid) {
            model.addAttribute("error", "Invalid OTP");
            return "otp";
        }

        admin.setTwoFactorEnabled(true);
        adminRepository.save(admin);

        // ✅ mark this session as verified
        request.getSession()
                .setAttribute("ADMIN_2FA_VERIFIED", true);

        return "redirect:/admin/dashboard";
    }
}
