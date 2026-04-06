package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.cart.CartService;
import com.shreeganesh.enterprises.entity.CartItemEntity;
import com.shreeganesh.enterprises.entity.OrderTable;
import com.shreeganesh.enterprises.entity.OrderItem;
import com.shreeganesh.enterprises.repository.OrderRepository;
import com.shreeganesh.enterprises.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/*  @Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    // Checkout Page
    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {

        String email = principal.getName();

        model.addAttribute("cart", cartService.getCart(email));
        model.addAttribute("total", cartService.totalAmount(email));

        return "checkout";
    }

    // Place Order
    @PostMapping("/place")
    public String placeOrder(@RequestParam String paymentMethod,
                             Principal principal) {

        String email = principal.getName();

        List<CartItemEntity> cartItems = cartService.getCart(email);

        OrderTable order = new OrderTable();
        order.setUserEmail(email);
        order.setTotalAmount(cartService.totalAmount(email));
        order.setPaymentMethod(paymentMethod);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemEntity c : cartItems) {
            OrderItem item = new OrderItem();
            item.setProductId(c.getProductId());
            item.setName(c.getName());
            item.setPrice(c.getPrice());
            item.setQuantity(c.getQuantity());
            item.setOrder(order);
            orderItems.add(item);
        }

        order.setItems(orderItems);

        orderRepository.save(order);

        emailService.sendOrderEmail(order);

        // Clear DB cart
        cartService.clear(email);

        return "redirect:/order/success";
    }

    // Success Page
    @GetMapping("/success")
    public String success() {
        return "order-success";
    }
}
*/