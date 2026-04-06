package com.shreeganesh.enterprises.controller;


import com.shreeganesh.enterprises.repository.OrderRepository;
import com.shreeganesh.enterprises.entity.OrderTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.List;

/*@Controller
public class OrderHistoryController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/my-orders")
    public String myOrders(Model model, Principal principal) {

        String userEmail = principal.getName();
        List<OrderTable> orders = orderRepository.findByUserEmail(userEmail);

        model.addAttribute("orders", orders);

        return "my-orders";
    }
}
*/
