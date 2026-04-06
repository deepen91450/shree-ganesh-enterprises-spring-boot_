/* package com.shreeganesh.enterprises.controller;


import com.shreeganesh.enterprises.entity.Enquiry;
import com.shreeganesh.enterprises.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AdminReplyController {

    @Autowired
    private EnquiryRepository enquiryRepository;

    @GetMapping("/admin/enquiries/reply/{id}")
    public String reply(@PathVariable Long id, Model model) {
        Enquiry enquiry = enquiryRepository.findById(id).orElse(null);
        model.addAttribute("enquiry", enquiry);
        return "reply-form";
    }
}
*/

