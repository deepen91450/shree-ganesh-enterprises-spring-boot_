package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.cart.CartService;
import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    /* ----------------------------------------------------------------------
     * 1. ADD TO CART (NON-AJAX)
     * ---------------------------------------------------------------------- */
    @GetMapping("/add/{id}")
    public String add(@PathVariable Long id,
                      @RequestParam(defaultValue = "1") int qty,
                      Principal principal,
                      HttpSession session) {

        if (principal == null) {
            return "redirect:/login";
        }

        Product product = productRepository.findById(id).orElse(null);

        // 🔒 BLOCK ENQUIRY-ONLY PRODUCTS
        if (product == null || !product.isPriceVisible()) {
            return "redirect:/contact?productId=" + id;
        }

        cartService.addToCart(product, qty, principal.getName());

        // 🔓 Unlock enquiry when cart changes
        session.removeAttribute("CART_ENQUIRY_SUBMITTED");

        return "redirect:/products";
    }

    /* ----------------------------------------------------------------------
     * 1B. ADD TO CART (AJAX)
     * ---------------------------------------------------------------------- */
    @PostMapping("/ajax/add")
    @ResponseBody
    public Map<String, Object> ajaxAdd(
            @RequestParam Long id,
            @RequestParam(defaultValue = "1") int qty,
            Principal principal,
            HttpSession session,
            HttpServletRequest request) {

        if (principal == null) {
            // ✅ Pass the page the user was on so JS can redirect back after login
            String referer = request.getHeader("Referer");
            String redirectUrl = (referer != null && !referer.isBlank())
                    ? "/login?redirect=" + java.net.URLEncoder.encode(referer, java.nio.charset.StandardCharsets.UTF_8)
                    : "/login";

            return Map.of(
                    "error", "not_logged_in",
                    "redirect", redirectUrl
            );
        }

        Product product = productRepository.findById(id).orElse(null);

        // 🔒 BLOCK ENQUIRY-ONLY PRODUCTS
        if (product == null || !product.isPriceVisible()) {
            return Map.of(
                    "error", "enquiry_only",
                    "redirect", "/contact?productId=" + id
            );
        }

        cartService.addToCart(product, qty, principal.getName());

        // 🔓 Unlock enquiry when cart changes
        session.removeAttribute("CART_ENQUIRY_SUBMITTED");

        return Map.of(
                "count",
                cartService.totalItemsCount(principal.getName())
        );
    }

    /* ----------------------------------------------------------------------
     * 2. VIEW CART
     * ---------------------------------------------------------------------- */
    @GetMapping("/view")
    public String view(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        String email = principal.getName();
        model.addAttribute("cart", cartService.getCart(email));
        model.addAttribute("total", cartService.totalAmount(email));

        return "cart";
    }

    /* ----------------------------------------------------------------------
     * 3. UPDATE QUANTITY (AJAX)
     * ---------------------------------------------------------------------- */
    @PostMapping("/update/{productId}")
    @ResponseBody
    public Map<String, Object> updateQty(
            @PathVariable Long productId,
            @RequestParam int qty,
            Principal principal,
            HttpSession session) {

        if (principal == null) {
            return Map.of("error", "Unauthorized");
        }

        String email = principal.getName();

        cartService.setQuantity(email, productId, qty);

        // 🔓 Unlock enquiry when cart changes
        session.removeAttribute("CART_ENQUIRY_SUBMITTED");

        return Map.of(
                "itemTotal", cartService.itemTotal(email, productId),
                "grandTotal", cartService.totalAmount(email),
                "count", cartService.totalItemsCount(email)
        );
    }

    /* ----------------------------------------------------------------------
     * 4. REMOVE ITEM (AJAX)
     * ---------------------------------------------------------------------- */
    @PostMapping("/remove/{productId}")
    @ResponseBody
    public Map<String, Object> removeItem(
            @PathVariable Long productId,
            Principal principal,
            HttpSession session) {

        if (principal == null) {
            return Map.of("error", "Unauthorized");
        }

        String email = principal.getName();
        cartService.remove(email, productId);

        // 🔓 Unlock enquiry when cart changes
        session.removeAttribute("CART_ENQUIRY_SUBMITTED");

        return Map.of(
                "grandTotal", cartService.totalAmount(email),
                "count", cartService.totalItemsCount(email)
        );
    }

    /* ----------------------------------------------------------------------
     * 5. CART COUNT (HEADER BADGE)
     * ---------------------------------------------------------------------- */
    @GetMapping("/count")
    @ResponseBody
    public Map<String, Integer> count(Principal principal) {

        if (principal == null) {
            return Map.of("count", 0);
        }

        return Map.of(
                "count",
                cartService.totalItemsCount(principal.getName())
        );
    }
}