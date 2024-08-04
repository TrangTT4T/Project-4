package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
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
        logger.info("Attempting to find user with ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    logger.info("User found with ID {}: {}", id, user.getUsername());
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    logger.info("User not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        logger.info("Attempting to find user by username: {}", username);
        User user = userRepository.findByUsername(username);
        if (user == null) {
            logger.info("User not found with username: {}", username);
            return ResponseEntity.notFound().build();
        } else {
            logger.info("User found with username {}: {}", username, user.getId());
            return ResponseEntity.ok(user);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        logger.info("Attempting to create user with username: {}", createUserRequest.getUsername());

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        if (createUserRequest.getPassword().length() < 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            logger.info("Password validation failed for username have length less than 7: {}", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }

        if (userRepository.findByUsername(createUserRequest.getUsername()) != null) {
            logger.info("Username already exists: {}", createUserRequest.getUsername());
            return ResponseEntity.badRequest().build();
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);

        logger.info("User created successfully with username: {}", createUserRequest.getUsername());
        return ResponseEntity.ok(user);
    }

}
