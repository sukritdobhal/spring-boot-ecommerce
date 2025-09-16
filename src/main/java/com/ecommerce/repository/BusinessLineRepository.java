package com.ecommerce.repository;

import com.ecommerce.entity.BusinessLine;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BusinessLineRepository extends MongoRepository<BusinessLine, String> {
    Optional<BusinessLine> findByName(String name);
}