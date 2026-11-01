package com.shreeganesh.enterprises.ai;

import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.entity.ProductEnquiry;
import com.shreeganesh.enterprises.entity.ProductStatus;

import com.shreeganesh.enterprises.repository.CategoryRepository;
import com.shreeganesh.enterprises.repository.EnquiryRepository;
import com.shreeganesh.enterprises.repository.ProductEnquiryRepository;
import com.shreeganesh.enterprises.repository.ProductRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.List;

@Component
@Profile("ai")
@Transactional(readOnly = true)
public class AiBusinessTools {

    private static final int MAX_RESULTS = 10;
    private static final int LOW_STOCK_THRESHOLD = 5;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EnquiryRepository enquiryRepository;
    private final ProductEnquiryRepository productEnquiryRepository;

    public AiBusinessTools(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            EnquiryRepository enquiryRepository,
            ProductEnquiryRepository productEnquiryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.enquiryRepository = enquiryRepository;
        this.productEnquiryRepository = productEnquiryRepository;
    }

    @Tool(description = """
            Get actual product statistics from the business database.
            Returns total products and counts of active, draft and inactive products.
            Use this when the administrator asks about product totals or status.
            """)
    public String getProductStatistics() {

        return """
                PRODUCT STATISTICS
                Total products: %d
                Active products: %d
                Draft products: %d
                Inactive products: %d
                """.formatted(
                productRepository.count(),
                productRepository.countByStatus(ProductStatus.ACTIVE),
                productRepository.countByStatus(ProductStatus.DRAFT),
                productRepository.countByStatus(ProductStatus.INACTIVE)
        );
    }

    @Tool(description = """
            Search the product catalogue by product name or description.
            Use this when the administrator asks to find or list particular products.
            """)
    public String searchProducts(
            @ToolParam(description = "Product name or search keyword")
            String keyword) {

        if (keyword == null || keyword.isBlank() || keyword.length() > 80) {
            return "Please provide a valid product search keyword.";
        }

        List<Product> products = productRepository.search(keyword.trim());

        if (products.isEmpty()) {
            return "No matching products were found.";
        }

        StringBuilder result = new StringBuilder("MATCHING PRODUCTS:\n");

        products.stream()
                .limit(MAX_RESULTS)
                .forEach(product -> result.append(formatProduct(product)));

        if (products.size() > MAX_RESULTS) {
            result.append("Only the first ")
                    .append(MAX_RESULTS)
                    .append(" matching products are shown.\n");
        }

        return result.toString();
    }

    @Tool(description = """
            Find products with low stock.
            The default low-stock threshold is five units.
            Use this when the administrator asks about low stock or products
            that are running low.
            """)
    public String getLowStockProducts() {

        List<Product> products =
                productRepository.findLowStockProducts(
                        LOW_STOCK_THRESHOLD,
                        PageRequest.of(0, MAX_RESULTS)
                );

        if (products.isEmpty()) {
            return "No products with low stock were found.";
        }

        StringBuilder result = new StringBuilder(
                "LOW STOCK PRODUCTS (1 to 5 units):\n"
        );

        products.forEach(product ->
                result.append(formatProduct(product))
        );

        return result.toString();
    }

    @Tool(description = """
            Find products that currently have zero stock.
            Use this when the administrator asks which products are out of stock.
            """)
    public String getOutOfStockProducts() {

        List<Product> products = productRepository.findAll(
                PageRequest.of(0, 100)
        ).getContent();

        List<Product> outOfStock = products.stream()
                .filter(product -> product.getStockQuantity() <= 0)
                .limit(MAX_RESULTS)
                .toList();

        if (outOfStock.isEmpty()) {
            return "No out-of-stock products were found in the retrieved records.";
        }

        StringBuilder result = new StringBuilder("OUT OF STOCK PRODUCTS:\n");

        outOfStock.forEach(product ->
                result.append(formatProduct(product))
        );

        return result.toString();
    }

    @Tool(description = """
            Get the total number of categories and search category names.
            Use this for questions about product categories.
            """)
    public String searchCategories(
            @ToolParam(description = """
                    Search term for a category. Use an empty string
                    when the administrator asks for all categories.
                    """)
            String keyword) {

        if (keyword == null || keyword.isBlank()) {

            return "Total categories: " + categoryRepository.count();
        }

        if (keyword.length() > 80) {
            return "Category search term is too long.";
        }

        var categories = categoryRepository.search(keyword.trim());

        if (categories.isEmpty()) {
            return "No matching categories were found.";
        }

        StringBuilder result = new StringBuilder("MATCHING CATEGORIES:\n");

        categories.stream()
                .limit(MAX_RESULTS)
                .forEach(category ->
                        result.append("- ")
                                .append(category.getName())
                                .append("\n")
                );

        return result.toString();
    }

    @Tool(description = """
            Get actual contact enquiry and product enquiry statistics,
            including unanswered enquiry counts.
            Use this for enquiry statistics and pending reply questions.
            """)
    public String getEnquiryStatistics() {

        return """
                ENQUIRY STATISTICS

                Contact enquiries: %d
                Unanswered contact enquiries: %d

                Product enquiries: %d
                Unanswered product enquiries: %d
                """.formatted(
                enquiryRepository.count(),
                enquiryRepository.countByAdminReplyNull(),
                productEnquiryRepository.count(),
                productEnquiryRepository.countByAdminReplyNull()
        );
    }

    @Tool(description = """
            Get the latest contact enquiries and product enquiries.
            Returns enquiry IDs, dates, reply status and product names.
            Does not return customer email addresses or phone numbers.
            """)
    public String getRecentEnquiries() {

        var contacts = enquiryRepository.findAllByOrderByCreatedAtDesc(
                PageRequest.of(0, 5)
        ).getContent();

        var productEnquiries =
                productEnquiryRepository.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(0, 5)
                ).getContent();

        StringBuilder result = new StringBuilder();

        result.append("RECENT CONTACT ENQUIRIES:\n");

        if (contacts.isEmpty()) {
            result.append("No contact enquiries found.\n");
        }

        contacts.forEach(enquiry ->
                result.append("- ID: ")
                        .append(enquiry.getId())
                        .append(" | Date: ")
                        .append(enquiry.getCreatedAt())
                        .append(" | Status: ")
                        .append(enquiry.getAdminReply() == null
                                ? "Unanswered" : "Replied")
                        .append("\n")
        );

        result.append("\nRECENT PRODUCT ENQUIRIES:\n");

        if (productEnquiries.isEmpty()) {
            result.append("No product enquiries found.\n");
        }

        for (ProductEnquiry enquiry : productEnquiries) {

            result.append("- ID: ")
                    .append(enquiry.getId())
                    .append(" | Date: ")
                    .append(enquiry.getCreatedAt())
                    .append(" | Status: ")
                    .append(enquiry.getAdminReply() == null
                            ? "Unanswered" : "Replied")
                    .append(" | Products: ");

            if (enquiry.getItems() == null || enquiry.getItems().isEmpty()) {
                result.append("No products recorded");
            } else {
                result.append(
                        enquiry.getItems().stream()
                                .map(item -> item.getProductName()
                                        + " x " + item.getQuantity())
                                .toList()
                );
            }

            result.append("\n");
        }

        return result.toString();
    }

    private String formatProduct(Product product) {

        return "- Name: " + product.getName()
                + " | Status: " + product.getStatus()
                + " | Stock: " + product.getStockQuantity()
                + " | Category: "
                + (product.getCategory() == null
                ? "Uncategorised"
                : product.getCategory().getName())
                + "\n";
    }
}