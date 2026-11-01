package com.shreeganesh.enterprises.ai;

import com.shreeganesh.enterprises.entity.Category;
import com.shreeganesh.enterprises.entity.Enquiry;
import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.entity.ProductEnquiry;
import com.shreeganesh.enterprises.entity.ProductEnquiryItem;
import com.shreeganesh.enterprises.entity.ProductStatus;

import com.shreeganesh.enterprises.repository.CategoryRepository;
import com.shreeganesh.enterprises.repository.EnquiryRepository;
import com.shreeganesh.enterprises.repository.ProductEnquiryRepository;
import com.shreeganesh.enterprises.repository.ProductRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AiBusinessDataService {

    private static final int MAX_PRODUCTS = 20;
    private static final int MAX_CATEGORIES = 30;
    private static final int MAX_RECENT_ENQUIRIES = 5;
    private static final int LOW_STOCK_THRESHOLD = 5;

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EnquiryRepository enquiryRepository;
    private final ProductEnquiryRepository productEnquiryRepository;

    public AiBusinessDataService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            EnquiryRepository enquiryRepository,
            ProductEnquiryRepository productEnquiryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.enquiryRepository = enquiryRepository;
        this.productEnquiryRepository = productEnquiryRepository;
    }

    /**
     * Retrieves only the business information relevant to the question.
     */
    public String getBusinessContext(String question) {

        String q = question.toLowerCase(Locale.ROOT);
        StringBuilder context = new StringBuilder();

        boolean summary = containsAny(
                q, "business summary", "business overview",
                "business statistics", "dashboard summary"
        );

        boolean productQuestion = containsAny(
                q, "product", "stock", "inventory",
                "catalog", "catalogue"
        );

        boolean categoryQuestion = containsAny(
                q, "categor", "subcategory", "sub-category"
        );

        boolean enquiryQuestion = containsAny(
                q, "enquiry", "enquiries", "inquiry",
                "inquiries", "customer message", "contact form"
        );

        if (summary) {
            appendBusinessSummary(context);
        }

        if (productQuestion) {
            appendProductInformation(context, q);
        }

        if (categoryQuestion) {
            appendCategoryInformation(context);
        }

        if (enquiryQuestion) {
            appendEnquiryInformation(context, q);
        }

        if (context.isEmpty()) {
            return """
                    No business database information was retrieved for this question.
                    Do not invent product counts, stock quantities, customer enquiries,
                    or other company-specific facts.
                    """;
        }

        return context.toString();
    }

    private void appendBusinessSummary(StringBuilder context) {

        context.append("\n=== BUSINESS SUMMARY ===\n");

        appendProductStatistics(context);

        context.append("Total categories: ")
                .append(categoryRepository.count())
                .append("\n");

        context.append("Total contact enquiries: ")
                .append(enquiryRepository.count())
                .append("\n");

        context.append("Unanswered contact enquiries: ")
                .append(enquiryRepository.countByAdminReplyNull())
                .append("\n");

        context.append("Total product enquiries: ")
                .append(productEnquiryRepository.count())
                .append("\n");

        context.append("Unanswered product enquiries: ")
                .append(productEnquiryRepository.countByAdminReplyNull())
                .append("\n");
    }

    private void appendProductStatistics(StringBuilder context) {

        context.append("\n=== PRODUCT STATISTICS ===\n");

        context.append("Total products: ")
                .append(productRepository.count())
                .append("\n");

        context.append("Active products: ")
                .append(productRepository.countByStatus(ProductStatus.ACTIVE))
                .append("\n");

        context.append("Draft products: ")
                .append(productRepository.countByStatus(ProductStatus.DRAFT))
                .append("\n");

        context.append("Inactive products: ")
                .append(productRepository.countByStatus(ProductStatus.INACTIVE))
                .append("\n");
    }

    private void appendProductInformation(
            StringBuilder context, String question) {

        appendProductStatistics(context);

        List<Product> products = productRepository.findAll();

        boolean lowStockQuestion = containsAny(
                question, "low stock", "running low",
                "almost out of stock"
        );

        boolean outOfStockQuestion = containsAny(
                question, "out of stock", "no stock", "zero stock"
        );

        boolean stockQuestion = containsAny(
                question, "stock", "inventory", "quantity"
        );

        boolean listQuestion = containsAny(
                question, "list", "show", "display", "which products",
                "all products", "product names"
        );

        if (!lowStockQuestion && !outOfStockQuestion
                && !stockQuestion && !listQuestion) {
            return;
        }

        context.append("\n=== PRODUCT DETAILS ===\n");

        if (lowStockQuestion) {

            List<Product> lowStockProducts = products.stream()
                    .filter(p -> p.getStockQuantity() > 0
                            && p.getStockQuantity() <= LOW_STOCK_THRESHOLD)
                    .limit(MAX_PRODUCTS)
                    .toList();

            context.append("Low-stock threshold: ")
                    .append(LOW_STOCK_THRESHOLD)
                    .append(" units or fewer, excluding zero-stock products.\n");

            appendProductList(context, lowStockProducts);

        } else if (outOfStockQuestion) {

            List<Product> outOfStockProducts = products.stream()
                    .filter(p -> p.getStockQuantity() <= 0)
                    .limit(MAX_PRODUCTS)
                    .toList();

            appendProductList(context, outOfStockProducts);

        } else {

            appendProductList(
                    context,
                    products.stream().limit(MAX_PRODUCTS).toList()
            );

            if (products.size() > MAX_PRODUCTS) {
                context.append("Only the first ")
                        .append(MAX_PRODUCTS)
                        .append(" products are shown.\n");
            }
        }
    }

    private void appendProductList(
            StringBuilder context, List<Product> products) {

        if (products.isEmpty()) {
            context.append("No matching products found.\n");
            return;
        }

        for (Product product : products) {

            context.append("- ")
                    .append(product.getName())
                    .append(" | Status: ")
                    .append(product.getStatus())
                    .append(" | Stock: ")
                    .append(product.getStockQuantity());

            if (product.getCategory() != null) {
                context.append(" | Category: ")
                        .append(product.getCategory().getName());
            }

            context.append("\n");
        }
    }

    private void appendCategoryInformation(StringBuilder context) {

        context.append("\n=== CATEGORIES ===\n");

        List<Category> categories = categoryRepository.findAll();

        context.append("Total categories: ")
                .append(categories.size())
                .append("\n");

        categories.stream()
                .limit(MAX_CATEGORIES)
                .forEach(category -> {

                    context.append("- ")
                            .append(category.getName());

                    if (category.getParent() != null) {
                        context.append(" | Parent category: ")
                                .append(category.getParent().getName());
                    }

                    context.append("\n");
                });

        if (categories.size() > MAX_CATEGORIES) {
            context.append("Only the first ")
                    .append(MAX_CATEGORIES)
                    .append(" categories are shown.\n");
        }
    }

    private void appendEnquiryInformation(
            StringBuilder context, String question) {

        context.append("\n=== CONTACT ENQUIRIES ===\n");

        context.append("Total contact enquiries: ")
                .append(enquiryRepository.count())
                .append("\n");

        context.append("Unanswered contact enquiries: ")
                .append(enquiryRepository.countByAdminReplyNull())
                .append("\n");

        context.append("\n=== PRODUCT ENQUIRIES ===\n");

        context.append("Total product enquiries: ")
                .append(productEnquiryRepository.count())
                .append("\n");

        context.append("Unanswered product enquiries: ")
                .append(productEnquiryRepository.countByAdminReplyNull())
                .append("\n");

        boolean recentQuestion = containsAny(
                question, "latest", "recent", "last five",
                "last 5", "show", "list", "details",
                "summarise", "summarize"
        );

        boolean pendingQuestion = containsAny(
                question, "pending", "unanswered",
                "not replied", "not answered"
        );

        if (!recentQuestion && !pendingQuestion) {
            return;
        }

        context.append("\n=== RECENT CONTACT ENQUIRIES ===\n");

        List<Enquiry> contactEnquiries =
                enquiryRepository.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(0, MAX_RECENT_ENQUIRIES)
                ).getContent();

        if (contactEnquiries.isEmpty()) {
            context.append("No contact enquiries found.\n");
        }

        for (Enquiry enquiry : contactEnquiries) {

            context.append("- Enquiry ID: ")
                    .append(enquiry.getId())
                    .append(" | Name: ")
                    .append(enquiry.getName())
                    .append(" | Date: ")
                    .append(enquiry.getCreatedAt())
                    .append(" | Reply status: ")
                    .append(enquiry.getAdminReply() == null
                            ? "Unanswered" : "Replied")
                    .append(" | Message: ")
                    .append(shorten(enquiry.getMessage()))
                    .append("\n");
        }

        context.append("\n=== RECENT PRODUCT ENQUIRIES ===\n");

        List<ProductEnquiry> productEnquiries =
                productEnquiryRepository.findAllByOrderByCreatedAtDesc(
                        PageRequest.of(0, MAX_RECENT_ENQUIRIES)
                ).getContent();

        if (productEnquiries.isEmpty()) {
            context.append("No product enquiries found.\n");
        }

        for (ProductEnquiry enquiry : productEnquiries) {

            context.append("- Enquiry ID: ")
                    .append(enquiry.getId())
                    .append(" | Customer: ")
                    .append(enquiry.getUserName())
                    .append(" | Date: ")
                    .append(enquiry.getCreatedAt())
                    .append(" | Reply status: ")
                    .append(enquiry.getAdminReply() == null
                            ? "Unanswered" : "Replied")
                    .append(" | Products: ");

            List<ProductEnquiryItem> items = enquiry.getItems();

            if (items == null || items.isEmpty()) {
                context.append("No product items recorded");
            } else {
                context.append(
                        items.stream()
                                .map(item -> item.getProductName()
                                        + " (quantity: "
                                        + item.getQuantity() + ")")
                                .collect(Collectors.joining(", "))
                );
            }

            context.append(" | Message: ")
                    .append(shorten(enquiry.getMessage()))
                    .append("\n");
        }
    }

    private boolean containsAny(String text, String... keywords) {

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    private String shorten(String text) {

        if (text == null || text.isBlank()) {
            return "No message";
        }

        String cleaned = text.replaceAll("\\s+", " ").trim();

        if (cleaned.length() > 180) {
            return cleaned.substring(0, 180) + "...";
        }

        return cleaned;
    }
}