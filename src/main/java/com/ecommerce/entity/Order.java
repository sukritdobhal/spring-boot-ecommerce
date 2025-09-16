package com.ecommerce.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private List<OrderItem> items;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String status;

    // Constructors
    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = "PLACED";
    }

    public Order(String userId, List<OrderItem> items, Double totalAmount) {
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = "PLACED";
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Inner class for OrderItem
    public static class OrderItem {
        private String productId;
        private String productName;
        private Integer quantity;
        private Double priceAtPurchase;

        public OrderItem() {}

        public OrderItem(String productId, String productName, Integer quantity, Double priceAtPurchase) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.priceAtPurchase = priceAtPurchase;
        }

        // Getters and Setters
        public String getProductId() { return productId; }
        public void setProductId(String productId) { this.productId = productId; }

        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public Double getPriceAtPurchase() { return priceAtPurchase; }
        public void setPriceAtPurchase(Double priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
    }
}