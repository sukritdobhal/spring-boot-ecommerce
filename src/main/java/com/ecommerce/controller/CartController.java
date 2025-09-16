package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.dto.CartItemRequest;
import com.ecommerce.entity.CartItem;
import com.ecommerce.security.UserPrincipal;
import com.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CartItem>> getCartItems(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userPrincipal.getId());
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addToCart(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                       @Valid @RequestBody CartItemRequest request) {
        try {
            CartItem cartItem = cartService.addToCart(
                    userPrincipal.getId(),
                    request.getProductId(),
                    request.getQuantity()
            );
            return ResponseEntity.ok(cartItem);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to add item to cart: " + e.getMessage(), false));
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateCartItem(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @Valid @RequestBody CartItemRequest request) {
        try {
            CartItem cartItem = cartService.updateCartItemQuantity(
                    userPrincipal.getId(),
                    request.getProductId(),
                    request.getQuantity()
            );

            if (cartItem == null) {
                return ResponseEntity.ok(new ApiResponse("Item removed from cart"));
            } else {
                return ResponseEntity.ok(cartItem);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to update cart item: " + e.getMessage(), false));
        }
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFromCart(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                            @PathVariable String productId) {
        try {
            cartService.removeFromCart(userPrincipal.getId(), productId);
            return ResponseEntity.ok(new ApiResponse("Item removed from cart"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to remove item from cart: " + e.getMessage(), false));
        }
    }

    @DeleteMapping("/clear")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> clearCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            cartService.clearCart(userPrincipal.getId());
            return ResponseEntity.ok(new ApiResponse("Cart cleared"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("Failed to clear cart: " + e.getMessage(), false));
        }
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Integer> getCartItemCount(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        int count = cartService.getCartItemCount(userPrincipal.getId());
        return ResponseEntity.ok(count);
    }

    @GetMapping("/validate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> validateCart(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        boolean isValid = cartService.validateCartInventory(userPrincipal.getId());
        return ResponseEntity.ok(new ApiResponse(
                isValid ? "Cart is valid" : "Some items in cart are out of stock",
                isValid
        ));
    }
}
