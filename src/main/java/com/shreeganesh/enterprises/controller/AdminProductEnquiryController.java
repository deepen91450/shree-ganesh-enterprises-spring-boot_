package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.ProductEnquiry;
import com.shreeganesh.enterprises.entity.ProductEnquiryItem;
import com.shreeganesh.enterprises.repository.ProductEnquiryRepository;
import com.shreeganesh.enterprises.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Controller
@RequestMapping("/admin/enquiries")
public class AdminProductEnquiryController {

    @Autowired
    private ProductEnquiryRepository productEnquiryRepository;

    @Autowired
    private EmailService emailService;

    // ✅ LIST PRODUCT ENQUIRIES
    @GetMapping("/products")
    public String listProductEnquiries(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int pageSize = 10;

        Page<ProductEnquiry> enquiryPage =
                productEnquiryRepository.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(page, pageSize)
                );

        model.addAttribute("requests", enquiryPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", enquiryPage.getTotalPages());

        model.addAttribute("content", "admin/enquiries/products");

        return "admin/layout";
    }

    // ✅ SHOW REPLY FORM
    @GetMapping("/products/reply/{id}")
    public String showProductReplyForm(@PathVariable Long id, Model model) {

        ProductEnquiry enquiry = productEnquiryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product Enquiry not found with ID: " + id));

        model.addAttribute("enquiry", enquiry);
        model.addAttribute("content", "admin/enquiries/product-reply");

        return "admin/layout";
    }

    // ✅ SEND REPLY
    @PostMapping("/products/reply/{id}")
    public String sendProductReply(
            @PathVariable Long id,
            @RequestParam("replyMessage") String replyMessage) {

        ProductEnquiry enquiry = productEnquiryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Product Enquiry not found with ID: " + id));

        /* ---------------- CLEAN MESSAGE ---------------- */
        String cleanMessage = (replyMessage == null || replyMessage.trim().isEmpty())
                ? "No reply message added"
                : replyMessage.trim();

        /* ---------------- APPEND REPLY (MULTIPLE REPLIES) ---------------- */
        String existingReply = enquiry.getAdminReply();

        if (existingReply == null || existingReply.isBlank()) {
            enquiry.setAdminReply(cleanMessage);
        } else {
            enquiry.setAdminReply(
                    existingReply
                            + "\n\n---\n"
                            + "Replied at: " + LocalDateTime.now()
                            + "\n"
                            + cleanMessage
            );
        }

        enquiry.setReplyAt(LocalDateTime.now());
        productEnquiryRepository.save(enquiry);

        /* ---------------- SEND PROFESSIONAL HTML EMAIL ---------------- */
        String userEmail = enquiry.getUserEmail(); // or enquiry.getEmail()

        if (userEmail != null && !userEmail.isBlank()) {

            // Build product list (NO images)
            StringBuilder productHtml = new StringBuilder();

            for (ProductEnquiryItem item : enquiry.getItems()) {
                productHtml.append("""
                    <tr>
                        <td style="padding:8px;border:1px solid #ddd;">
                            %s
                        </td>
                        <td style="padding:8px;border:1px solid #ddd;text-align:center;">
                            %d
                        </td>
                    </tr>
                """.formatted(
                        item.getProductName(),
                        item.getQuantity()
                ));
            }

            // HTML email body
            String htmlBody = """
            <!DOCTYPE html>
            <html>
            <body style="font-family:Arial;background:#f4f6f8;padding:20px;">

            <div style="max-width:650px;margin:auto;background:#ffffff;
                        border-radius:8px;
                        box-shadow:0 2px 8px rgba(0,0,0,0.08);">

                <div style="padding:25px;color:#333;">
                    <h2 style="margin-top:0;color:#0f172a;">
                        Reply to Your Product Enquiry
                    </h2>

                    <p>Hello,</p>

                    <p>Our team has replied to your product enquiry.</p>

                    <div style="background:#f1f5f9;
                                border-left:4px solid #2563eb;
                                padding:15px;
                                margin:15px 0;">
                        %s
                    </div>

                    <h4>Enquired Products</h4>

                    <table style="width:100%%;border-collapse:collapse;font-size:14px;">
                        <thead>
                            <tr style="background:#f8fafc;">
                                <th style="padding:8px;border:1px solid #ddd;text-align:left;">
                                    Product
                                </th>
                                <th style="padding:8px;border:1px solid #ddd;">
                                    Qty
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>

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
                    productHtml.toString(),
                    java.time.Year.now().getValue()
            );

            // Send HTML email
            emailService.sendHtmlEmail(
                    userEmail,
                    "Reply to Your Product Enquiry",
                    htmlBody
            );
        }

        return "redirect:/admin/enquiries/products?success=productReply";
    }
}
