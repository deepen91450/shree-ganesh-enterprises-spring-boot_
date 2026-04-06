package com.shreeganesh.enterprises.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_enquiry_items")
public class ProductEnquiryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private String productName;
    private String productImage;
    private double productPrice;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "enquiry_id")
    private ProductEnquiry enquiry;

    // ----------------------
    // GETTERS
    // ----------------------

    public Long getId() { return id; }

    public Long getProductId() { return productId; }

    public String getProductName() { return productName; }

    public String getProductImage() { return productImage; }

    public double getProductPrice() { return productPrice; }

    public int getQuantity() { return quantity; }

    public ProductEnquiry getEnquiry() { return enquiry; }


    // ----------------------
    // SETTERS
    // ----------------------

    public void setId(Long id) { this.id = id; }

    public void setProductId(Long productId) { this.productId = productId; }

    public void setProductName(String productName) { this.productName = productName; }

    public void setProductImage(String productImage) { this.productImage = productImage; }

    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void setEnquiry(ProductEnquiry enquiry) { this.enquiry = enquiry; }
}
