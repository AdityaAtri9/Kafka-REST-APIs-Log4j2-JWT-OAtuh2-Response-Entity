package com.jobportal.jobportal_api.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.jobportal_api.config.JwtUtils;
import com.jobportal.jobportal_api.entity.Role;
import com.jobportal.jobportal_api.entity.User;
import com.jobportal.jobportal_api.repository.RoleRepository;
import com.jobportal.jobportal_api.repository.UserRepository;

@Service
public class AuthService {
    private static final Logger logger = LogManager.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepo;
    
    @Autowired
    private RoleRepository roleRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authManager;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    
    public Optional<User> registerLocal(User userRaw) {
        logger.info("Register attempt for username: {}", userRaw.getUsername());

        if (userRepo.existsByUsername(userRaw.getUsername())) {
            logger.warn("Username exists: {}", userRaw.getUsername());
            return Optional.empty();
        }
        if (userRepo.existsByEmail(userRaw.getEmail())) {
            logger.warn("Email exists: {}", userRaw.getEmail());
            return Optional.empty();
        }

        userRaw.setPassword(passwordEncoder.encode(userRaw.getPassword()));
        Role role = roleRepo.findByName("ROLE_USER").orElseGet(() -> roleRepo.save(new Role(null,"ROLE_USER")));
        userRaw.getRole().add(role);
        userRepo.save(userRaw);
        logger.info("User registered: {}", userRaw.getUsername());
        return Optional.of(userRaw);
    }

    public Optional<String> login(String username, String password) {
        logger.info("Login attempt for username: {}", username);
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            if (auth.isAuthenticated()) {
                String token = jwtUtils.generateToken(username);
                logger.info("Authentication successful for {} - token issued", username);
                return Optional.of(token);
            } else {
                logger.warn("Authentication failed for {}", username);
                return Optional.empty();
            }
        } catch (BadCredentialsException ex) {
            logger.warn("Bad credentials for {}", username);
            return Optional.empty();
        } catch (Exception ex) {
            logger.error("Authentication error for {}: {}", username, ex.getMessage());
            return Optional.empty();
        }
    }
}