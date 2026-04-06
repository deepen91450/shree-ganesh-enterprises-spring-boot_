package com.shreeganesh.enterprises.entity;

import jakarta.persistence.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    // ✅ Used only when priceVisible = true
    private double price;

    // ✅ NEW FIELD (for enquiry-only products)
    @Column(nullable = false)
    private boolean priceVisible = true;

    private String imagePath;

    // CATEGORY RELATION
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // ⭐ CKEditor Full HTML Description
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String longDescription;

    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.ACTIVE;

    // -------------------
    // GETTERS & SETTERS
    // -------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // ✅ NEW
    public boolean isPriceVisible() {
        return priceVisible;
    }

    public void setPriceVisible(boolean priceVisible) {
        this.priceVisible = priceVisible;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
