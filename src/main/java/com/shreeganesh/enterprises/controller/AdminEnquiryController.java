/*package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.repository.ProductEnquiryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminEnquiryController {

    @Autowired
    private ProductEnquiryRepository productEnquiryRepository;

    @GetMapping("/enquiries")
    public String viewEnquiries(Model model) {
        model.addAttribute("enquiries", productEnquiryRepository.findAll());
        return "admin-enquiries";
    }

    @GetMapping("/product-enquiry/reply/{id}")
    public String showReplyForm(@PathVariable Long id, Model model) {

        var enquiry = productEnquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquiry not found"));

        model.addAttribute("enquiry", enquiry);
        return "admin-enquiry-reply";
    }

    @PostMapping("/product-enquiry/reply/{id}")
    public String saveReply(
            @PathVariable Long id,
            @RequestParam String reply) {

        var enquiry = productEnquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquiry not found"));

        enquiry.setAdminReply(reply);
        enquiry.setReplyAt(LocalDateTime.now());
        productEnquiryRepository.save(enquiry);

        return "redirect:/admin/enquiries?success=reply_saved";
    }
}

*/