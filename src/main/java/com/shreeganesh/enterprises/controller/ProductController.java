/* package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.Category;
import com.shreeganesh.enterprises.service.CategoryService;
import com.shreeganesh.enterprises.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // ======================= CATEGORY PRODUCTS ===========================
    @GetMapping("/category/{id}")
    public String productsByCategory(@PathVariable Long id, Model model) {

        Category category = categoryService.get(id);

        if (category == null) {
            return "redirect:/";  // Avoid crash if category not found
        }

        model.addAttribute("category", category);
        model.addAttribute("products", productService.getByCategory(id));
        model.addAttribute("subcategories", category.getChildren());


        return "products-by-category";  // This HTML must exist in templates/
    }
}
*/