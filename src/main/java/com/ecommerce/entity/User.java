package com.ecommerce.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.util.Set;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;
    private String email;
    private String businessLineId;
    private Double discountRate;
    private Set<String> roles;

    // Constructors
    public User() {}

    public User(String username, String password, String email, String businessLineId, Double discountRate, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.businessLineId = businessLineId;
        this.discountRate = discountRate;
        this.roles = roles;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBusinessLineId() { return businessLineId; }
    public void setBusinessLineId(String businessLineId) { this.businessLineId = businessLineId; }

    public Double getDiscountRate() { return discountRate; }
    public void setDiscountRate(Double discountRate) { this.discountRate = discountRate; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}