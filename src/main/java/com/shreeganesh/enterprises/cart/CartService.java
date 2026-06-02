package com.shreeganesh.enterprises.cart;

import com.shreeganesh.enterprises.entity.CartItemEntity;
import com.shreeganesh.enterprises.entity.Product;
import com.shreeganesh.enterprises.repository.CartRepository;
import com.shreeganesh.enterprises.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;


    /* ----------------------------------------------------------
     * GET CART ITEMS
     * ---------------------------------------------------------- */
    public List<CartItemEntity> getCart(String userEmail) {
        return cartRepository.findByUserEmail(userEmail);
    }

    /* ----------------------------------------------------------
     * ADD TO CART
     * ---------------------------------------------------------- */
    @Transactional
    public void addToCart(Product product, int qty, String email) {

        if (qty <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }

        int maxLimit = (product.getMaxOrderQty() != null)
                ? product.getMaxOrderQty()
                : 200;

        int allowedQty = Math.min(product.getStockQuantity(), maxLimit);

        CartItemEntity existing =
                cartRepository.findByUserEmailAndProductId(email, product.getId());

        if (existing != null) {

            int newQty = existing.getQuantity() + qty;

            if (newQty > allowedQty) {
                throw new IllegalArgumentException(
                        "Maximum allowed quantity is " + allowedQty
                );
            }

            existing.setQuantity(newQty);
            cartRepository.save(existing);
            return;
        }

        if (qty > allowedQty) {
            throw new IllegalArgumentException(
                    "You can only add up to " + allowedQty + " items"
            );
        }

        CartItemEntity newItem = new CartItemEntity();
        newItem.setProductId(product.getId());
        newItem.setUserEmail(email);
        newItem.setName(product.getName());
        newItem.setPrice(product.getPrice());
        newItem.setQuantity(qty);
        newItem.setImagePath(product.getImagePath());

        cartRepository.save(newItem);
    }
    /* ----------------------------------------------------------
     * REMOVE ITEM
     * ---------------------------------------------------------- */
    @Transactional
    public void remove(String email, Long productId) {
        cartRepository.deleteByUserEmailAndProductId(email, productId);
    }

    /* ----------------------------------------------------------
     * CART TOTAL
     * ---------------------------------------------------------- */
    public double totalAmount(String email) {
        return getCart(email).stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
    }

    /* ----------------------------------------------------------
     * CLEAR CART
     * ---------------------------------------------------------- */
    @Transactional
    public void clear(String email) {
        cartRepository.deleteByUserEmail(email);
    }

    /* ----------------------------------------------------------
     * CHANGE QTY (+ or -)
     * ---------------------------------------------------------- */
    @Transactional
    public void changeQuantity(String email, Long productId, int change) {

        CartItemEntity item = cartRepository.findByUserEmailAndProductId(email, productId);
        if (item == null) return;

        int newQty = item.getQuantity() + change;

        if (newQty <= 0) {
            cartRepository.delete(item);
        } else {
            item.setQuantity(newQty);
            cartRepository.save(item);
        }
    }

    /* ----------------------------------------------------------
     * SET ABSOLUTE QUANTITY (used by /cart/update)
     * ---------------------------------------------------------- */
    @Transactional
    public void setQuantity(String email, Long productId, int qty) {

        CartItemEntity item =
                cartRepository.findByUserEmailAndProductId(email, productId);

        if (item == null) return;

        if (qty <= 0) {
            cartRepository.delete(item);
            return;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        int maxLimit = (product.getMaxOrderQty() != null)
                ? product.getMaxOrderQty()
                : 200;

        int allowedQty = Math.min(product.getStockQuantity(), maxLimit);

        if (qty > allowedQty) {
            throw new IllegalArgumentException(
                    "Maximum allowed quantity is " + allowedQty
            );
        }

        item.setQuantity(qty);
        cartRepository.save(item);
    }

    /* ----------------------------------------------------------
     * ITEM TOTAL (qty × price)
     * ---------------------------------------------------------- */
    public double itemTotal(String email, Long productId) {
        CartItemEntity item = cartRepository.findByUserEmailAndProductId(email, productId);
        if (item == null) return 0;
        return item.getPrice() * item.getQuantity();
    }

    /* ----------------------------------------------------------
     * TOTAL ITEMS COUNT (sum of all quantities)
     * ---------------------------------------------------------- */
    public int totalItemsCount(String email) {
        return getCart(email).stream()
                .mapToInt(CartItemEntity::getQuantity)
                .sum();
    }

}
