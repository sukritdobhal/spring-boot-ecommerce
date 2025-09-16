package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.exception.InsufficientInventoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private DiscountCalculationService discountCalculationService;

    // Use concurrent locks per product ID to avoid blocking unrelated products
    private final ConcurrentHashMap<String, ReentrantLock> productLocks = new ConcurrentHashMap<>();

    /**
     * Get or create lock for specific product
     */
    private ReentrantLock getProductLock(String productId) {
        return productLocks.computeIfAbsent(productId, k -> new ReentrantLock());
    }

    public List<Product> getProductsByBusinessLine(String businessLineId) {
        List<Product> products = productRepository.findByBusinessLineId(businessLineId);

        // Update current discount rates based on inventory
        products.forEach(product -> {
            double currentDiscountRate = discountCalculationService.calculateDynamicProductDiscountRate(product);
            product.setCurrentDiscountRate(currentDiscountRate);
        });

        return products;
    }

    public Optional<Product> getProductById(String productId) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            double currentDiscountRate = discountCalculationService.calculateDynamicProductDiscountRate(product);
            product.setCurrentDiscountRate(currentDiscountRate);
        }

        return productOpt;
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.findAll();

        // Update current discount rates based on inventory
        products.forEach(product -> {
            double currentDiscountRate = discountCalculationService.calculateDynamicProductDiscountRate(product);
            product.setCurrentDiscountRate(currentDiscountRate);
        });

        return products;
    }

    /**
     * Synchronized method to update inventory - prevents race conditions
     * This handles the scenario where two users try to order 10 items when only 10 are available
     */
    @Transactional
    public boolean updateInventory(String productId, int quantityToReduce) throws InsufficientInventoryException {
        ReentrantLock lock = getProductLock(productId);
        lock.lock();

        try {
            Optional<Product> productOpt = productRepository.findById(productId);

            if (productOpt.isPresent()) {
                Product product = productOpt.get();

                // Check if sufficient inventory is available
                if (product.getQuantity() < quantityToReduce) {
                    throw new InsufficientInventoryException(
                        "Insufficient inventory for " + product.getName() + 
                        ". Available: " + product.getQuantity() + 
                        ", Requested: " + quantityToReduce
                    );
                }

                // Update quantity
                product.setQuantity(product.getQuantity() - quantityToReduce);

                // Recalculate discount rate based on new inventory level
                double newDiscountRate = discountCalculationService.calculateDynamicProductDiscountRate(product);
                product.setCurrentDiscountRate(newDiscountRate);

                // Save updated product
                productRepository.save(product);

                System.out.println("Updated inventory for " + product.getName() + 
                                 ": quantity reduced by " + quantityToReduce + 
                                 ", new discount rate: " + newDiscountRate + "%");

                return true;
            }

            return false;

        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if requested quantity is available in stock
     */
    public boolean isStockAvailable(String productId, int requestedQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(product -> product.getQuantity() >= requestedQuantity).orElse(false);
    }

    /**
     * Get current available quantity for a product
     */
    public int getAvailableQuantity(String productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(Product::getQuantity).orElse(0);
    }
}