package com.ecommerce.service;

import com.ecommerce.entity.*;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.exception.InsufficientInventoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private DiscountCalculationService discountCalculationService;

    /**
     * Process order with inventory synchronization
     */
    @Transactional
    public Order placeOrder(String userId) throws InsufficientInventoryException {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get cart items
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // Validate inventory and calculate total
        List<Order.OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = productService.getProductById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Calculate final price with discounts
            BigDecimal finalPrice = discountCalculationService.calculateFinalPrice(product, user);
            BigDecimal itemTotal = finalPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            // Create order item
            Order.OrderItem orderItem = new Order.OrderItem(
                    product.getId(),
                    product.getName(),
                    cartItem.getQuantity(),
                    finalPrice.doubleValue()
            );

            orderItems.add(orderItem);
            totalAmount = totalAmount.add(itemTotal);
        }

        // Update inventory for all items (this is synchronized)
        for (CartItem cartItem : cartItems) {
            boolean updated = productService.updateInventory(cartItem.getProductId(), cartItem.getQuantity());
            if (!updated) {
                throw new RuntimeException("Failed to update inventory for product: " + cartItem.getProductId());
            }
        }

        // Create and save order
        Order order = new Order(userId, orderItems, totalAmount.doubleValue());
        order = orderRepository.save(order);

        // Clear cart after successful order
        cartService.clearCart(userId);

        return order;
    }

    public List<Order> getOrdersByUserId(String userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}