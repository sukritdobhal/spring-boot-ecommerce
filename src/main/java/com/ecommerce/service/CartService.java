package com.ecommerce.service;

import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    public List<CartItem> getCartItemsByUserId(String userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem addToCart(String userId, String productId, int quantity) {
        // Check if stock is available
        if (!productService.isStockAvailable(productId, quantity)) {
            throw new RuntimeException("Insufficient stock available");
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;

            // Check if combined quantity is available
            if (!productService.isStockAvailable(productId, newQuantity)) {
                throw new RuntimeException("Not enough stock for requested quantity");
            }

            cartItem.setQuantity(newQuantity);
            return cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setUserId(userId);
            newCartItem.setProductId(productId);
            newCartItem.setQuantity(quantity);
            return cartItemRepository.save(newCartItem);
        }
    }

    public void removeFromCart(String userId, String productId) {
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);
    }

    public CartItem updateCartItemQuantity(String userId, String productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeFromCart(userId, productId);
            return null;
        }

        // Check if stock is available for the new quantity
        if (!productService.isStockAvailable(productId, newQuantity)) {
            throw new RuntimeException("Insufficient stock for requested quantity");
        }

        Optional<CartItem> cartItemOpt = cartItemRepository.findByUserIdAndProductId(userId, productId);

        if (cartItemOpt.isPresent()) {
            CartItem cartItem = cartItemOpt.get();
            cartItem.setQuantity(newQuantity);
            return cartItemRepository.save(cartItem);
        }

        return null;
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    public boolean validateCartInventory(String userId) {
        List<CartItem> cartItems = getCartItemsByUserId(userId);

        for (CartItem item : cartItems) {
            if (!productService.isStockAvailable(item.getProductId(), item.getQuantity())) {
                return false;
            }
        }

        return true;
    }

    public int getCartItemCount(String userId) {
        List<CartItem> items = getCartItemsByUserId(userId);
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }
}