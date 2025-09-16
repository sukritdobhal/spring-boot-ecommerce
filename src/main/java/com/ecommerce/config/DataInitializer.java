package com.ecommerce.config;

import com.ecommerce.entity.BusinessLine;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.BusinessLineRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private BusinessLineRepository businessLineRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeBusinessLines();
        initializeProducts();
        initializeUsers();
    }

    private void initializeBusinessLines() {
        if (businessLineRepository.count() == 0) {
            BusinessLine buss1 = new BusinessLine("Buss1", "Humanities and Social Sciences Books");
            BusinessLine buss2 = new BusinessLine("Buss2", "Physics and Mathematics Books");

            businessLineRepository.save(buss1);
            businessLineRepository.save(buss2);

            System.out.println("Business lines initialized");
        }
    }

    private void initializeProducts() {
        if (productRepository.count() == 0) {
            // Get business lines
            BusinessLine buss1 = businessLineRepository.findByName("Buss1").orElse(null);
            BusinessLine buss2 = businessLineRepository.findByName("Buss2").orElse(null);

            if (buss1 != null) {
                // Buss1 products
                Product product1 = new Product("PhilosophyBooks", buss1.getId(), 25.99, 100, 100, 10.0, 
                        "Collection of philosophical works and theories");
                Product product2 = new Product("SpiritualityBooks", buss1.getId(), 29.99, 80, 80, 12.0, 
                        "Spiritual guidance and meditation books");
                Product product3 = new Product("SociologyBooks", buss1.getId(), 34.99, 60, 60, 8.0, 
                        "Social behavior and society analysis books");
                Product product4 = new Product("HistoryBooks", buss1.getId(), 27.99, 90, 90, 15.0, 
                        "Historical events and civilizations");

                productRepository.save(product1);
                productRepository.save(product2);
                productRepository.save(product3);
                productRepository.save(product4);
            }

            if (buss2 != null) {
                // Buss2 products
                Product product5 = new Product("QuantumMathsBook", buss2.getId(), 45.99, 50, 50, 5.0, 
                        "Advanced quantum mathematics and calculations");
                Product product6 = new Product("TheoryOfRelativityBook", buss2.getId(), 39.99, 40, 40, 7.0, 
                        "Einstein's theory of relativity explained");
                Product product7 = new Product("QuantumMechanicsFundamentals", buss2.getId(), 49.99, 30, 30, 6.0, 
                        "Fundamentals of quantum mechanics");
                Product product8 = new Product("StringTheory", buss2.getId(), 52.99, 25, 25, 4.0, 
                        "String theory in theoretical physics");

                productRepository.save(product5);
                productRepository.save(product6);
                productRepository.save(product7);
                productRepository.save(product8);
            }

            System.out.println("Products initialized");
        }
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            // Get business lines
            BusinessLine buss1 = businessLineRepository.findByName("Buss1").orElse(null);
            BusinessLine buss2 = businessLineRepository.findByName("Buss2").orElse(null);

            Set<String> userRole = new HashSet<>();
            userRole.add("USER");

            if (buss1 != null && buss2 != null) {
                // Create test users
                User userA = new User("userA", passwordEncoder.encode("password123"), 
                        "userA@example.com", buss1.getId(), 15.0, userRole);
                User userB = new User("userB", passwordEncoder.encode("password123"), 
                        "userB@example.com", buss1.getId(), 10.0, userRole);
                User userC = new User("userC", passwordEncoder.encode("password123"), 
                        "userC@example.com", buss2.getId(), 20.0, userRole);

                userRepository.save(userA);
                userRepository.save(userB);
                userRepository.save(userC);

                System.out.println("Test users initialized:");
                System.out.println("- userA: password123, Business Line: Buss1, Discount: 15%");
                System.out.println("- userB: password123, Business Line: Buss1, Discount: 10%");
                System.out.println("- userC: password123, Business Line: Buss2, Discount: 20%");
            }
        }
    }
}
