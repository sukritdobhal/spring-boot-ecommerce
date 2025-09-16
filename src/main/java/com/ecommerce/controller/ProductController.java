package com.ecommerce.controller;

import com.ecommerce.dto.ApiResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/business-line/{businessLineId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Product>> getProductsByBusinessLine(@PathVariable String businessLineId) {
        List<Product> products = productService.getProductsByBusinessLine(businessLineId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/availability")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> checkProductAvailability(@PathVariable String id, @RequestParam int quantity) {
        boolean isAvailable = productService.isStockAvailable(id, quantity);
        int availableQuantity = productService.getAvailableQuantity(id);

        return ResponseEntity.ok(new ProductAvailabilityResponse(
                isAvailable,
                availableQuantity,
                isAvailable ? "Stock available" : "Insufficient stock"
        ));
    }

    // Inner class for product availability response
    public static class ProductAvailabilityResponse {
        private boolean available;
        private int availableQuantity;
        private String message;

        public ProductAvailabilityResponse(boolean available, int availableQuantity, String message) {
            this.available = available;
            this.availableQuantity = availableQuantity;
            this.message = message;
        }

        // Getters
        public boolean isAvailable() { return available; }
        public int getAvailableQuantity() { return availableQuantity; }
        public String getMessage() { return message; }
    }
}
