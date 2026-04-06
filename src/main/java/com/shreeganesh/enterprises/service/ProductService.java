package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.entity.ProductStatus;
import com.shreeganesh.enterprises.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    public void save(Product product) {
        repo.save(product);
    }

    public List<Product> getAllProducts() {
        return repo.findAll();
    }

    public void deleteProduct(Long id) {
        Product product = repo.findById(id).orElse(null);

        if (product != null) {

            // 1️⃣ DELETE IMAGE FILE FROM DISK
            String imagePath = product.getImagePath(); // e.g. /uploads/abc.jpg

            if (imagePath != null && !imagePath.isBlank()) {
                Path filePath = Paths.get(
                        "F:/enterprises/uploads/" +
                                imagePath.replace("/uploads/", "")
                );

                try {
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // 2️⃣ DELETE PRODUCT FROM DATABASE
            repo.deleteById(id);
        }
    }


    public Product getProductById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // GET BY CATEGORY
    public List<Product> getByCategory(Long categoryId) {
        return repo.findByCategoryId(categoryId);
    }






    // FIX: search now uses correct repository
    public List<Product> search(String keyword) {
        return repo.searchProducts(keyword);
    }



    public Page<Product> getPaginatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAll(pageable);
    }

    // ✅ Dashboard counts (SAFE)
    public long countAll() {
        return repo.count();
    }

    public long countByStatus(ProductStatus status) {
        return repo.countByStatus(status);
    }

    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByNameContainingIgnoreCase(keyword, pageable);
    }


}
