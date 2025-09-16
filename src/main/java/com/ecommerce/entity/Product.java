package com.ecommerce.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private String businessLineId;
    private Double basePrice;
    private Integer quantity;
    private Integer initialQuantity;
    private Double initialDiscountRate;
    private Double currentDiscountRate;
    private String description;

    // Constructors
    public Product() {}

    public Product(String name, String businessLineId, Double basePrice, Integer quantity, 
                   Integer initialQuantity, Double initialDiscountRate, String description) {
        this.name = name;
        this.businessLineId = businessLineId;
        this.basePrice = basePrice;
        this.quantity = quantity;
        this.initialQuantity = initialQuantity;
        this.initialDiscountRate = initialDiscountRate;
        this.currentDiscountRate = initialDiscountRate;
        this.description = description;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBusinessLineId() { return businessLineId; }
    public void setBusinessLineId(String businessLineId) { this.businessLineId = businessLineId; }

    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getInitialQuantity() { return initialQuantity; }
    public void setInitialQuantity(Integer initialQuantity) { this.initialQuantity = initialQuantity; }

    public Double getInitialDiscountRate() { return initialDiscountRate; }
    public void setInitialDiscountRate(Double initialDiscountRate) { this.initialDiscountRate = initialDiscountRate; }

    public Double getCurrentDiscountRate() { return currentDiscountRate; }
    public void setCurrentDiscountRate(Double currentDiscountRate) { this.currentDiscountRate = currentDiscountRate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}