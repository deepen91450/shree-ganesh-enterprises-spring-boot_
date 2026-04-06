package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.Category;
import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.service.CategoryService;
import com.shreeganesh.enterprises.service.HeroBannerService;
import com.shreeganesh.enterprises.service.ProductService;

import com.shreeganesh.enterprises.service.WhyChooseUsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private HeroBannerService heroBannerService;

    @Autowired
    private WhyChooseUsService whyChooseUsService;


    // ================= HOME =================
    @GetMapping("/")
    public String home(Model model) {

        model.addAttribute("categories", categoryService.getRootCategories());
        model.addAttribute("banners", heroBannerService.getActiveBanners());

        model.addAttribute("whyList", whyChooseUsService.getActiveItems());


        return "user/home"; // ✅ FULL PAGE
    }

    // ================= PRODUCTS =================
    @GetMapping("/products")
    public String products(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size) {

        Page<Product> productPage = productService.getPaginatedProducts(page, size);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("categories", categoryService.getRootCategories());

        return "user/products"; // ✅ FULL PAGE
    }

    // ================= PRODUCT DETAILS =================
    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {

        Product product = productService.getProductById(id);
        model.addAttribute("product", product);

        return "user/product-details"; // ✅ FULL PAGE
    }

    // ================= CATEGORIES =================
    @GetMapping("/categories")
    public String showCategories(Model model) {

        model.addAttribute("categories", categoryService.getRootCategories());

        return "user/categories"; // ✅ FULL PAGE
    }

    // ================= CATEGORY VIEW =================
    @GetMapping("/categories/{id}")
    public String viewCategory(@PathVariable Long id, Model model) {

        Category category = categoryService.get(id);

        model.addAttribute("category", category);
        model.addAttribute("subcategories", categoryService.getChildren(id));
        model.addAttribute("products", productService.getByCategory(id));

        return "user/category-view"; // ✅ FULL PAGE
    }

    // ================= SEARCH =================
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model
    ) {

        Page<Product> productPage =
                productService.searchProducts(keyword, page, size);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "user/search-results";
    }

}
