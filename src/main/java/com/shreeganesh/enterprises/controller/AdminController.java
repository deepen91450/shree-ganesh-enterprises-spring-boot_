package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.Admin;
import com.shreeganesh.enterprises.entity.Notification;
import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.entity.ProductStatus;
import com.shreeganesh.enterprises.repository.AdminRepository;
import com.shreeganesh.enterprises.repository.EnquiryRepository;
import com.shreeganesh.enterprises.repository.ProductEnquiryRepository;
import com.shreeganesh.enterprises.service.CategoryService;
import com.shreeganesh.enterprises.service.HtmlSanitizerService;
import com.shreeganesh.enterprises.service.NotificationService;
import com.shreeganesh.enterprises.service.ProductService;
import com.shreeganesh.enterprises.service.UploadStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UploadStorageService uploadStorageService;

    @Autowired
    private HtmlSanitizerService htmlSanitizerService;

    // 🔔 GLOBAL NOTIFICATIONS (AVAILABLE IN ALL PAGES)
    @ModelAttribute("notifications")
    public List<Notification> getNotifications() {
        return notificationService.getUnread();
    }
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

    // 🔔 MARK ALL NOTIFICATIONS AS READ
    @PostMapping("/mark-read")
    public String markNotificationsAsRead() {
        notificationService.markAllAsRead();
        return "redirect:/admin/dashboard";
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
        product.setLongDescription(
                htmlSanitizerService.sanitizeProductDescription(product.getLongDescription())
        );

        String imagePath = uploadStorageService.storeImage(imageFile, "");
        if (imagePath != null) {
            product.setImagePath(imagePath);
        }

        productService.save(product);
        return "redirect:/admin/dashboard";
    }


    // ================= DELETE PRODUCT =================
    @PostMapping("/delete/{id}")
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
        existingProduct.setLongDescription(
                htmlSanitizerService.sanitizeProductDescription(product.getLongDescription())
        );

        existingProduct.setMaxOrderQty(product.getMaxOrderQty());

        if (!imageFile.isEmpty()) {

            // 1️⃣ DELETE OLD IMAGE FILE
            uploadStorageService.deletePublicFile(existingProduct.getImagePath());

            // 2️⃣ SAVE NEW IMAGE
            String uploadedImagePath = uploadStorageService.storeImage(imageFile, "");

            // 3️⃣ UPDATE DB PATH
            existingProduct.setImagePath(uploadedImagePath);
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


    // ================= ADD STOCK PAGE =================
    @GetMapping("/add-stock/{id}")
    public String showAddStockPage(@PathVariable Long id, Model model) {

        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("content", "admin/products/add-stock");

        return "admin/layout";
    }


    // ================= HANDLE ADD STOCK =================
    @PostMapping("/add-stock")
    public String addStock(@RequestParam Long productId,
                           @RequestParam int quantity) {

        productService.addStock(productId, quantity);
        return "redirect:/admin/products";
    }


    // ================= REDUCE STOCK PAGE =================
    @GetMapping("/reduce-stock/{id}")
    public String showReduceStockPage(@PathVariable Long id, Model model) {

        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/admin/products";
        }

        model.addAttribute("product", product);
        model.addAttribute("content", "admin/products/reduce-stock");

        return "admin/layout";
    }


    // ================= HANDLE REDUCE STOCK =================
    @PostMapping("/reduce-stock")
    public String reduceStock(@RequestParam Long productId,
                              @RequestParam int quantity) {

        productService.reduceStock(productId, quantity);
        return "redirect:/admin/products";
    }


    // ================= CKEDITOR IMAGE UPLOAD =================
    @PostMapping("/upload-image")
    @ResponseBody
    public Map<String, Object> uploadImage(@RequestParam("upload") MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        try {
            String publicPath = uploadStorageService.storeImage(file, "ckeditor");
            response.put("uploaded", 1);
            response.put("url", publicPath);

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

    @GetMapping("/change-password")
    public String showChangePasswordPage(Model model) {
        model.addAttribute("content", "admin/change-password");
        return "admin/layout";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            Authentication authentication,
            RedirectAttributes ra,
            HttpServletRequest request) {

        String email = authentication.getName();

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 🔐 Step 1: verify old password
        if (!encoder.matches(oldPassword, admin.getPassword())) {
            ra.addFlashAttribute("error", "Old password is incorrect");
            return "redirect:/admin/change-password";
        }

        // 🔐 Step 2: validate new password (basic security)
        if (newPassword.length() < 8) {
            ra.addFlashAttribute("error", "Password must be at least 8 characters");
            return "redirect:/admin/change-password";
        }

        // 🔐 Step 3: encode and save
        admin.setPassword(encoder.encode(newPassword));
        adminRepository.save(admin);

       // 🔐 logout user after password change
        request.getSession().invalidate();

        return "redirect:/admin/login?passwordChanged";
    }



}
