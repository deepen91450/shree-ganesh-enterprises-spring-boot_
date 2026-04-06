package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.Admin;
import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.entity.ProductStatus;
import com.shreeganesh.enterprises.repository.AdminRepository;
import com.shreeganesh.enterprises.repository.EnquiryRepository;
import com.shreeganesh.enterprises.repository.ProductEnquiryRepository;
import com.shreeganesh.enterprises.service.CategoryService;
import com.shreeganesh.enterprises.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EnquiryRepository enquiryRepository;

    @Autowired
    private ProductEnquiryRepository productEnquiryRepository;

    private static final String UPLOAD_DIR = "F:/enterprises/uploads/";

    // ================= LOGIN =================
    @GetMapping("/login")
    public String loginPage() {
        return "admin-login";
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {

        String email = authentication.getName();
        Admin admin = adminRepository.findByEmail(email).orElseThrow();

        if (!admin.isTwoFactorEnabled()) {
            return "redirect:/admin/2fa";
        }

        model.addAttribute("totalProducts", productService.countAll());
        model.addAttribute("activeProducts", productService.countByStatus(ProductStatus.ACTIVE));
        model.addAttribute("totalCategories", categoryService.countAll());
        model.addAttribute("newEnquiries", enquiryRepository.countByAdminReplyNull());

        model.addAttribute("content", "admin/dashboard");
        return "admin/layout";
    }

    // ================= ADD PRODUCT (FORM) =================
    @GetMapping("/add")
    public String addProduct(Model model) {
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("content", "admin/products/add");
        return "admin/layout";
    }

    // ================= ADD PRODUCT (SAVE) =================
    @PostMapping("/add")
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "priceVisible", required = false) Boolean priceVisible,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {

        product.setCategory(categoryService.get(categoryId));

        // 🔥 THIS LINE FIXES EVERYTHING
        product.setPriceVisible(priceVisible != null);

        if (!imageFile.isEmpty()) {
            String fileName = imageFile.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, imageFile.getBytes());
            product.setImagePath("/uploads/" + fileName);
        }

        productService.save(product);
        return "redirect:/admin/dashboard";
    }


    // ================= DELETE PRODUCT =================
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/dashboard";
    }

    // ================= EDIT PRODUCT (FORM) =================
    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {

        Product product = productService.getProductById(id);

        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("content", "admin/products/edit");

        return "admin/layout";
    }

    // ================= EDIT PRODUCT (SAVE) =================
    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            @RequestParam(value = "priceVisible", required = false) Boolean priceVisible,
            @RequestParam("image") MultipartFile imageFile
    ) throws IOException {


        Product existingProduct = productService.getProductById(id);

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setPriceVisible(priceVisible != null);
        existingProduct.setCategory(product.getCategory());
        existingProduct.setLongDescription(product.getLongDescription());

        if (!imageFile.isEmpty()) {

            // 1️⃣ DELETE OLD IMAGE FILE
            String oldImagePath = existingProduct.getImagePath();
            if (oldImagePath != null && !oldImagePath.isBlank()) {

                Path oldFile = Paths.get(
                        UPLOAD_DIR + oldImagePath.replace("/uploads/", "")
                );

                Files.deleteIfExists(oldFile);
            }

            // 2️⃣ SAVE NEW IMAGE
            String fileName = System.currentTimeMillis() + "_" +
                    imageFile.getOriginalFilename();

            Path newPath = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(newPath.getParent());
            Files.write(newPath, imageFile.getBytes());

            // 3️⃣ UPDATE DB PATH
            existingProduct.setImagePath("/uploads/" + fileName);
        }


        productService.save(existingProduct);
        return "redirect:/admin/dashboard?updated=true";
    }

    // ================= PRODUCTS LIST =================
    @GetMapping("/products")
    public String products(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        int size = 20;
        Page<Product> productPage;

        if (!keyword.trim().isEmpty()) {
            productPage = productService.searchProducts(keyword, page, size);
        } else {
            productPage = productService.getPaginatedProducts(page, size);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        model.addAttribute("content", "admin/products");
        return "admin/layout";
    }

    @GetMapping("/products/{id}")
    public String viewProductDetails(@PathVariable Long id, Model model) {

        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("content", "admin/products/view");

        return "admin/layout";
    }


    // ================= CKEDITOR IMAGE UPLOAD =================
    @PostMapping("/upload-image")
    @ResponseBody
    public Map<String, Object> uploadImage(@RequestParam("upload") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        try {
            String uploadDir = UPLOAD_DIR + "ckeditor/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());

            response.put("uploaded", 1);
            response.put("url", "/uploads/ckeditor/" + fileName);

        } catch (Exception e) {
            response.put("uploaded", 0);
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Upload failed");
            response.put("error", error);
        }

        return response;
    }

    // ================= GLOBAL ADMIN SEARCH =================
    @GetMapping("/search")
    public String adminGlobalSearch(
            @RequestParam("q") String keyword,
            Model model
    ) {

        model.addAttribute("keyword", keyword);

        model.addAttribute("products",
                productService.search(keyword));

        model.addAttribute("categories",
                categoryService.search(keyword));

        model.addAttribute("productEnquiries",
                productEnquiryRepository.searchProductEnquiries(keyword));

        model.addAttribute("customerEnquiries",
                enquiryRepository.searchCustomerEnquiries(keyword));

        model.addAttribute("content", "admin/search-results");
        return "admin/layout";
    }
}