package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        logger.info("Received request to find user by ID: {}", id);
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        logger.info("Received request to find user by username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.warn("User not found for username: {}", username);
            return ResponseEntity.notFound().build();
        }
        logger.info("User found for username: {}", username);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        try {
            logger.info("Received request to create user with username: {}", createUserRequest.getUsername());

            User user = new User();
            user.setUsername(createUserRequest.getUsername());
            Cart cart = new Cart();
            cartRepository.save(cart);
            user.setCart(cart);
            if (createUserRequest.getPassword().length() < 7 ||
                    !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
                logger.warn("Password validation failed for username: {}", createUserRequest.getUsername());
                logger.error("CreateUser request failures for username: {}", createUserRequest.getUsername());
                return ResponseEntity.badRequest().build();
            }
            user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
            userRepository.save(user);
            logger.info("CreateUser request successes with username: {}", createUserRequest.getUsername());
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            logger.error("Error occurred during create user for username: {}: {}", createUserRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

}
