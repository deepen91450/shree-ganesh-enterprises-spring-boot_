package com.shreeganesh.enterprises.controller;

import com.shreeganesh.enterprises.entity.Category;
import com.shreeganesh.enterprises.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryAdminController {

    private static final String UPLOAD_DIR = "F:/enterprises/uploads/categories/";

    @Autowired
    private CategoryService categoryService;

    // ================= LIST CATEGORIES =================
    @GetMapping
    public String list(Model model) {

        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("content", "admin/categories");

        return "admin/layout";
    }

    // ================= ADD CATEGORY (FORM) =================
    @GetMapping("/add")
    public String addForm(Model model) {

        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("content", "admin/categories/add");

        return "admin/layout";
    }

    // ================= ADD CATEGORY (SAVE) =================
    @PostMapping("/add")
    public String save(@RequestParam String name,
                       @RequestParam(required = false) Long parentId,
                       @RequestParam("image") MultipartFile imageFile) throws IOException {

        Category c = new Category();
        c.setName(name);

        // Parent category
        if (parentId != null) {
            c.setParent(categoryService.get(parentId));
        }

        // Image upload
        if (!imageFile.isEmpty()) {

            String fileName = System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR);

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            Files.copy(
                    imageFile.getInputStream(),
                    path.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            c.setImagePath("/uploads/categories/" + fileName);
        }

        categoryService.save(c);
        return "redirect:/admin/categories";
    }

    // ================= EDIT CATEGORY (FORM) =================
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        Category category = categoryService.get(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }

        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("content", "admin/categories/edit");

        return "admin/layout";
    }

    // ================= EDIT CATEGORY (UPDATE) =================
    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam String name,
                         @RequestParam(required = false) Long parentId,
                         @RequestParam(value = "image", required = false) MultipartFile imageFile)
            throws IOException {

        Category c = categoryService.get(id);
        if (c == null) {
            return "redirect:/admin/categories";
        }

        c.setName(name);
        c.setParent(parentId != null ? categoryService.get(parentId) : null);

        // Update image only if new file uploaded
        if (imageFile != null && !imageFile.isEmpty()) {

            String fileName = System.currentTimeMillis() + "-" + imageFile.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR);

            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            Files.copy(
                    imageFile.getInputStream(),
                    path.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            c.setImagePath("/uploads/categories/" + fileName);
        }

        categoryService.save(c);
        return "redirect:/admin/categories";
    }

    // ================= DELETE CATEGORY =================
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }
}
