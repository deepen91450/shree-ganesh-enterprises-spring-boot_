package com.shreeganesh.enterprises.service;

import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.entity.ProductStatus;
import com.shreeganesh.enterprises.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repo;

    // ✅ NEW
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UploadStorageService uploadStorageService;

    @Autowired
    private HtmlSanitizerService htmlSanitizerService;

    public void save(Product product) {
        product.setLongDescription(
                htmlSanitizerService.sanitizeProductDescription(product.getLongDescription())
        );
        repo.save(product);
    }

    public List<Product> getAllProducts() {
        return sanitizeProducts(repo.findAll());
    }

    public void deleteProduct(Long id) {
        Product product = repo.findById(id).orElse(null);

        if (product != null) {

            // DELETE IMAGE FILE
            String imagePath = product.getImagePath();

            if (imagePath != null && !imagePath.isBlank()) {
                try {
                    uploadStorageService.deletePublicFile(imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            repo.deleteById(id);
        }
    }

    public Product getProductById(Long id) {
        return sanitizeProduct(repo.findById(id).orElse(null));
    }

    public List<Product> getByCategory(Long categoryId) {
        return sanitizeProducts(repo.findByCategoryId(categoryId));
    }

    public List<Product> search(String keyword) {
        return sanitizeProducts(repo.searchProducts(keyword));
    }

    public Page<Product> getPaginatedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAll(pageable).map(this::sanitizeProduct);
    }

    public long countAll() {
        return repo.count();
    }

    public long countByStatus(ProductStatus status) {
        return repo.countByStatus(status);
    }

    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findByNameContainingIgnoreCase(keyword, pageable).map(this::sanitizeProduct);
    }

    // ================= STOCK MANAGEMENT =================

    // ➕ Add Stock
    public void addStock(Long productId, int qty) {
        Product product = repo.findById(productId).orElse(null);

        if (product != null) {

            product.setStockQuantity(product.getStockQuantity() + qty);
            repo.save(product);

            // 🔔 LOW STOCK ALERT (optional after adding)
            if (product.getStockQuantity() <= 5) {
                notificationService.create("Low stock: " + product.getName());
            }
        }
    }

    // ➖ Reduce Stock
    public void reduceStock(Long productId, int qty) {
        Product product = repo.findById(productId).orElse(null);

        if (product != null) {

            if (product.getStockQuantity() < qty) {
                throw new RuntimeException("Not enough stock");
            }

            product.setStockQuantity(product.getStockQuantity() - qty);
            repo.save(product);

            // 🔔 NOTIFICATIONS
            if (product.getStockQuantity() == 0) {
                notificationService.create("Out of stock: " + product.getName());
            } else if (product.getStockQuantity() <= 5) {
                notificationService.create("Low stock: " + product.getName());
            }
        }
    }

    private List<Product> sanitizeProducts(List<Product> products) {
        products.forEach(this::sanitizeProduct);
        return products;
    }

    private Product sanitizeProduct(Product product) {
        if (product != null) {
            product.setLongDescription(
                    htmlSanitizerService.sanitizeProductDescription(product.getLongDescription())
            );
        }
        return product;
    }
}
