package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByBusinessLineId(String businessLineId);
    List<Product> findByNameContainingIgnoreCase(String name);
}