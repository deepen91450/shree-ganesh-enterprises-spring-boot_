package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.OrderTable;
import com.shreeganesh.enterprises.entity.OrderItem;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 1️⃣ Existing password reset mail
    public void sendPasswordReset(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Link");
        message.setText("Click the link to reset your password: " + resetLink);

        mailSender.send(message);
    }

    // 2️⃣ New: Admin order email notification
    public void sendOrderEmail(OrderTable order) {

        String adminEmail = "your-admin-email@gmail.com";  // CHANGE THIS

        StringBuilder body = new StringBuilder();
        body.append("A new order has been placed.\n\n")
                .append("Customer Email: ").append(order.getUserEmail()).append("\n")
                .append("Payment Method: ").append(order.getPaymentMethod()).append("\n")
                .append("Total Amount: ₹").append(order.getTotalAmount()).append("\n\n")
                .append("Ordered Items:\n");

        for (OrderItem item : order.getItems()) {
            body.append("- ").append(item.getName())
                    .append(" | Qty: ").append(item.getQuantity())
                    .append(" | Price: ₹").append(item.getPrice())
                    .append("\n");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminEmail);
        message.setSubject("New Order Received - Order ID: " + order.getId());
        message.setText(body.toString());

        mailSender.send(message);
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML
            helper.setFrom("Shree Ganesh Enterprises <shreeganeshenterprises1102@gmail.com>");

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
