package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class DiscountCalculationService {

    /**
     * Calculate current product discount rate based on inventory level
     * Formula: If quantity decreases by 10%, discount rate decreases by 20% of initial discount rate
     */
    public double calculateDynamicProductDiscountRate(Product product) {
        if (product.getInitialQuantity() == 0) {
            return 0.0;
        }

        // Calculate quantity decrease percentage
        double quantityDecrease = (double) (product.getInitialQuantity() - product.getQuantity()) 
                                 / product.getInitialQuantity();

        // For every 10% decrease in quantity, discount decreases by 20% of initial rate
        double discountReductionFactor = 0.2 * (quantityDecrease / 0.1);

        // Calculate new discount rate
        double newDiscountRate = product.getInitialDiscountRate() * (1 - discountReductionFactor);

        // Ensure discount rate never goes below 0%
        return Math.max(0.0, newDiscountRate);
    }

    /**
     * Calculate final price with layered discounts
     * Step 1: Apply product discount to base price
     * Step 2: Apply user discount to discounted price
     */
    public BigDecimal calculateFinalPrice(Product product, User user) {
        BigDecimal basePrice = BigDecimal.valueOf(product.getBasePrice());

        // Step 1: Apply dynamic product discount
        double productDiscountRate = calculateDynamicProductDiscountRate(product) / 100.0;
        BigDecimal productDiscountAmount = basePrice.multiply(BigDecimal.valueOf(productDiscountRate));
        BigDecimal priceAfterProductDiscount = basePrice.subtract(productDiscountAmount);

        // Step 2: Apply user-specific discount
        double userDiscountRate = user.getDiscountRate() / 100.0;
        BigDecimal userDiscountAmount = priceAfterProductDiscount.multiply(BigDecimal.valueOf(userDiscountRate));
        BigDecimal finalPrice = priceAfterProductDiscount.subtract(userDiscountAmount);

        // Round to 2 decimal places
        return finalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate final price using discount rates directly
     */
    public BigDecimal calculateFinalPrice(Double basePrice, Double productDiscountRate, Double userDiscountRate) {
        BigDecimal price = BigDecimal.valueOf(basePrice);

        // Apply product discount
        if (productDiscountRate != null && productDiscountRate > 0) {
            BigDecimal discount = price.multiply(BigDecimal.valueOf(productDiscountRate / 100.0));
            price = price.subtract(discount);
        }

        // Apply user discount
        if (userDiscountRate != null && userDiscountRate > 0) {
            BigDecimal discount = price.multiply(BigDecimal.valueOf(userDiscountRate / 100.0));
            price = price.subtract(discount);
        }

        return price.setScale(2, RoundingMode.HALF_UP);
    }
}