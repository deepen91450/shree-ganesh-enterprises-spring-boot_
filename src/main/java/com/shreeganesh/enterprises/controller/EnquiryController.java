package com.shreeganesh.enterprises.controller;

import org.springframework.ui.Model;

import com.shreeganesh.enterprises.entity.Enquiry;
import com.shreeganesh.enterprises.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;


@Controller
public class EnquiryController {

    @Autowired
    private EnquiryRepository enquiryRepository;

    @PostMapping("/submit-enquiry")
    public String submitEnquiry(
            @Valid @ModelAttribute Enquiry enquiry,
            BindingResult result,
            RedirectAttributes ra,
            Model model) {

        // ❌ If validation fails
        if (result.hasErrors()) {
            model.addAttribute("enquiry", enquiry);
            return "contact"; // or contact-us page
        }

        // ✅ If validation passes
        enquiryRepository.save(enquiry);

        ra.addFlashAttribute(
                "successMsg",
                "Thank you! Your enquiry has been sent successfully."
        );

        return "redirect:/contact";
    }


}
