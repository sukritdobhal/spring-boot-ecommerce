package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.exception.InsufficientInventoryException;
import com.ecommerce.security.UserPrincipal;
import com.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> placeOrder(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Order order = orderService.placeOrder(userPrincipal.getId());
            return ResponseEntity.ok(order);
        } catch (InsufficientInventoryException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Order failed: " + e.getMessage(), false));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to place order: " + e.getMessage(), false));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<Order> orders = orderService.getOrdersByUserId(userPrincipal.getId());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId,
                                          @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Order order = orderService.getOrderById(orderId);

            // Check if order belongs to the authenticated user
            if (!order.getUserId().equals(userPrincipal.getId())) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
