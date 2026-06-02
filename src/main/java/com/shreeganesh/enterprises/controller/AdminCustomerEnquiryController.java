package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.Enquiry;
import com.shreeganesh.enterprises.repository.EnquiryRepository;
import com.shreeganesh.enterprises.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


@Controller
@RequestMapping("/admin/enquiries")
public class AdminCustomerEnquiryController {

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Autowired
    private EmailService emailService;

    // ✅ LIST CUSTOMER ENQUIRIES
    @GetMapping("/customers")
    public String listCustomerEnquiries(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int pageSize = 10;

        Page<Enquiry> enquiryPage =
                enquiryRepository.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(page, pageSize)
                );

        model.addAttribute("enquiries", enquiryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", enquiryPage.getTotalPages());

        model.addAttribute("content", "admin/enquiries/customers");

        return "admin/layout";
    }

    // ✅ SHOW REPLY FORM
    @GetMapping("/customers/reply/{id}")
    public String showReplyForm(@PathVariable Long id, Model model) {

        Enquiry enquiry = enquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquiry not found"));

        model.addAttribute("enquiry", enquiry);
        model.addAttribute("content", "admin/enquiries/customer-reply");

        return "admin/layout";
    }

    // ✅ SEND REPLY
    @PostMapping("/customers/reply/{id}")
    public String sendReply(
            @PathVariable Long id,
            @RequestParam("replyMessage") String replyMessage) {

        Enquiry enquiry = enquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enquiry not found"));

        /* ---------------- CLEAN MESSAGE ---------------- */
        String cleanMessage = (replyMessage == null || replyMessage.trim().isEmpty())
                ? "No reply message added"
                : replyMessage.trim();

        enquiry.setAdminReply(cleanMessage);
        enquiry.setReplyAt(LocalDateTime.now());
        enquiryRepository.save(enquiry);

        /* ---------------- SEND BEAUTIFUL HTML EMAIL ---------------- */
        String htmlBody = """
        <!DOCTYPE html>
        <html>
        <body style="font-family:Arial;background:#f4f6f8;padding:20px;">

        <div style="max-width:600px;margin:auto;background:#ffffff;
                    border-radius:8px;
                    box-shadow:0 2px 8px rgba(0,0,0,0.08);">

            <div style="padding:25px;color:#333;">
                <h2 style="margin-top:0;color:#0f172a;">
                    Reply to Your Enquiry
                </h2>

                <p>Hello,</p>

                <p>Thank you for contacting us. Please find our response below:</p>

                <div style="background:#f1f5f9;
                            border-left:4px solid #2563eb;
                            padding:15px;
                            margin:15px 0;
                            font-size:14px;">
                    %s
                </div>

                <p>
                    If you need any further assistance, feel free to reply to this email.
                </p>

                <p style="margin-top:20px;">
                    Regards,<br>
                    <strong>Shree Ganesh Enterprises</strong>
                </p>
            </div>

            <div style="background:#f8fafc;
                        padding:12px;
                        font-size:12px;
                        color:#666;
                        text-align:center;">
                © %d Shree Ganesh Enterprises
            </div>

        </div>

        </body>
        </html>
        """.formatted(
                cleanMessage.replace("\n", "<br>"),
                java.time.Year.now().getValue()
        );

        emailService.sendHtmlEmail(
                enquiry.getEmail(),
                "Reply to Your Enquiry",
                htmlBody
        );

        // 🔁 Redirect back to enquiry list
        return "redirect:/admin/enquiries/customers?success=customerReply";
    }
}
