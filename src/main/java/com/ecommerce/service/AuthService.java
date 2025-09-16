package com.ecommerce.service;

import com.ecommerce.dto.JwtResponse;
import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtTokenProvider;
import com.ecommerce.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return new JwtResponse(jwt, 
                               userPrincipal.getId(),
                               userPrincipal.getUsername(),
                               userPrincipal.getEmail(),
                               userPrincipal.getBusinessLineId(),
                               userPrincipal.getDiscountRate(),
                               getStringRoles(userPrincipal));
    }

    public User registerUser(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setBusinessLineId(registerRequest.getBusinessLineId());

        // Set user discount rate based on business line and username
        user.setDiscountRate(getUserDiscountRate(registerRequest.getUsername(), registerRequest.getBusinessLineId()));

        // Set default role
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);

        return userRepository.save(user);
    }

    private Double getUserDiscountRate(String username, String businessLineId) {
        // Assign discount rates as specified in requirements
        switch (username.toLowerCase()) {
            case "usera":
                return 15.0;
            case "userb":
                return 10.0;
            case "userc":
                return 20.0;
            default:
                // Default discount rates based on business line for new users
                if ("buss1".equals(businessLineId)) {
                    return 12.0; // Default for Buss1
                } else if ("buss2".equals(businessLineId)) {
                    return 8.0;  // Default for Buss2
                } else {
                    return 5.0;  // Default general discount
                }
        }
    }

    private Set<String> getStringRoles(UserPrincipal userPrincipal) {
        Set<String> roles = new HashSet<>();
        userPrincipal.getAuthorities().forEach(authority -> {
            String role = authority.getAuthority();
            if (role.startsWith("ROLE_")) {
                role = role.substring(5); // Remove "ROLE_" prefix
            }
            roles.add(role);
        });
        return roles;
    }

    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}