package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.*;
import com.shreeganesh.enterprises.repository.*;
import com.shreeganesh.enterprises.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartEnquiryController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductEnquiryRepository productEnquiryRepository;

    @Autowired
    private EmailService emailService;

    /* ======================================================
       CONVERT CART TO PRODUCT ENQUIRY
    ====================================================== */
    @PostMapping("/cart/enquiry")
    public String convertCartToEnquiry(
            @RequestParam(required = false) String message,
            Principal principal,
            RedirectAttributes ra, HttpSession session) {

        if (principal == null) {
            return "redirect:/login";
        }

        Boolean alreadySubmitted =
                (Boolean) session.getAttribute("CART_ENQUIRY_SUBMITTED");

        if (Boolean.TRUE.equals(alreadySubmitted)) {
            ra.addFlashAttribute(
                    "successMsg",
                    "Your enquiry has already been sent."
            );
            return "redirect:/my-enquiries";
        }


        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItemEntity> cartItems =
                cartItemRepository.findByUserEmail(email);

        if (cartItems.isEmpty()) {
            ra.addFlashAttribute("errorMsg", "Your cart is empty!");
            return "redirect:/cart";
        }

        // -------- Create enquiry ----------
        ProductEnquiry enquiry = new ProductEnquiry();
        enquiry.setUserId(user.getId());
        enquiry.setUserName(user.getName());
        enquiry.setUserEmail(user.getEmail());
        enquiry.setUserPhone(user.getPhone());
        enquiry.setMessage(
                (message == null || message.isBlank())
                        ? "No additional message"
                        : message.trim()
        );

        List<ProductEnquiryItem> items = new ArrayList<>();

        for (CartItemEntity ci : cartItems) {

            Product product = productRepository
                    .findById(ci.getProductId())
                    .orElse(null);

            if (product == null) continue;

            ProductEnquiryItem pei = new ProductEnquiryItem();
            pei.setProductId(product.getId());
            pei.setProductName(product.getName());
            pei.setProductImage(product.getImagePath()); // stored for UI
            pei.setProductPrice(product.getPrice());
            pei.setQuantity(ci.getQuantity());
            pei.setEnquiry(enquiry);

            items.add(pei);
        }

        enquiry.setItems(items);
        productEnquiryRepository.save(enquiry);

        // 🔐 Lock further submissions for this cart
        session.setAttribute("CART_ENQUIRY_SUBMITTED", true);


        // ⭐⭐⭐ SEND HTML EMAIL CONFIRMATION ⭐⭐⭐
        StringBuilder html = new StringBuilder();

        html.append("<div style='font-family:Arial,sans-serif;max-width:600px;margin:auto;"
                + "border:1px solid #e5e7eb;border-radius:10px;overflow:hidden;'>");

        html.append("<div style='background:#0f172a;color:white;padding:18px;text-align:center;'>")
                .append("<h2 style='margin:0;'>Shree Ganesh Enterprises</h2>")
                .append("<p style='margin:5px 0 0;font-size:14px;opacity:.9;'>")
                .append("Product Enquiry Confirmation</p>")
                .append("</div>");

        html.append("<div style='padding:22px;background:#ffffff;'>");

        html.append("<p>Hello <strong>")
                .append(user.getName())
                .append("</strong>,</p>");

        html.append("<p>Thank you for contacting us. ")
                .append("We have received your product enquiry successfully.</p>");

        html.append("<div style='margin:16px 0;padding:14px;background:#f8fafc;"
                        + "border-left:4px solid #2563eb;'>")
                .append("<strong>Your Message:</strong><br>")
                .append(enquiry.getMessage())
                .append("</div>");

        html.append("<h3 style='margin-top:25px;'>Requested Products</h3>");

        html.append("<table style='width:100%;border-collapse:collapse;font-size:14px;'>")
                .append("<thead>")
                .append("<tr style='background:#f1f5f9;'>")
                .append("<th align='left' style='padding:8px;'>Product</th>")
                .append("<th align='center' style='padding:8px;'>Qty</th>")
                .append("<th align='right' style='padding:8px;'>Price</th>")
                .append("</tr>")
                .append("</thead><tbody>");

        for (ProductEnquiryItem item : items) {
            html.append("<tr>")
                    .append("<td style='padding:8px;border-bottom:1px solid #e5e7eb;'>")
                    .append(item.getProductName())
                    .append("</td>")
                    .append("<td align='center' style='padding:8px;border-bottom:1px solid #e5e7eb;'>")
                    .append(item.getQuantity())
                    .append("</td>")
                    .append("<td align='right' style='padding:8px;border-bottom:1px solid #e5e7eb;'>₹")
                    .append(item.getProductPrice())
                    .append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table>");

        html.append("<p style='margin-top:20px;'>")
                .append("Our team will review your enquiry and get back to you shortly.")
                .append("</p>");

        html.append("<p style='margin-top:25px;'>")
                .append("Regards,<br><strong>Shree Ganesh Enterprises</strong>")
                .append("</p>");

        html.append("</div></div>");

        emailService.sendHtmlEmail(
                user.getEmail(),
                "Your Product Enquiry Confirmation",
                html.toString()
        );

        // -------- Clear cart ----------
        cartItemRepository.deleteAll(cartItems);

        ra.addFlashAttribute(
                "successMsg",
                "Your enquiry has been sent successfully!"
        );

        return "redirect:/my-enquiries";
    }

    /* ======================================================
       MY ENQUIRIES PAGE
    ====================================================== */
    @GetMapping("/my-enquiries")
    public String myEnquiries(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute(
                "productEnquiries",
                productEnquiryRepository.findByUserId(user.getId())
        );

        return "my-enquiries";
    }

    @PostMapping("/my-enquiries")
    public String handleMyEnquiriesPost() {
        return "redirect:/my-enquiries";
    }
}
