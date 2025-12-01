package com.jobportal.jobportal_api.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.jobportal_api.entity.User;
import com.jobportal.jobportal_api.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LogManager.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        logger.info("Register API called for {}", user.getUsername());
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            logger.warn("Register validation failed");
            return ResponseEntity.badRequest().body("username, email and password are required");
        }
        return authService.registerLocal(user)
                .<ResponseEntity<?>>map(u -> ResponseEntity.ok(u))
                .orElseGet(() -> ResponseEntity.badRequest().body("username or email already exists"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("username and password required");
        }
        logger.info("Login API called for {}", username);
        return authService.login(username, password)
                .<ResponseEntity<?>>map(token -> ResponseEntity.ok(Map.of("token", token)))
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid credentials"));
    }
}