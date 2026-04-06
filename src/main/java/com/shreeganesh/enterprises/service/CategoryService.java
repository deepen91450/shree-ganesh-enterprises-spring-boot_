package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.Category;
import com.shreeganesh.enterprises.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepo;

    // Get all categories
    public List<Category> getAll() {
        return categoryRepo.findAll();
    }

    // Get by ID
    public Category get(Long id) {
        return categoryRepo.findById(id).orElse(null);
    }

    // Save new category
    public void save(Category category) {
        categoryRepo.save(category);
    }

    // Update category
    public void updateCategory(Long id, String name, Long parentId) {

        Category category = get(id);
        if (category == null) return;

        category.setName(name);

        if (parentId != null) {
            Category parent = get(parentId);
            category.setParent(parent);
        } else {
            category.setParent(null); // ROOT
        }

        categoryRepo.save(category);
    }

    // Delete category
    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }

    // Root categories
    public List<Category> getRootCategories() {
        return categoryRepo.findByParentIsNull();
    }

    // Children categories
    public List<Category> getChildren(Long parentId) {
        return categoryRepo.findByParentId(parentId);
    }


    // Count all categories (for dashboard)
    public long countAll() {
        return categoryRepo.count();
    }


    public List<Category> search(String keyword) {
        return categoryRepo.search(keyword);
    }


}
