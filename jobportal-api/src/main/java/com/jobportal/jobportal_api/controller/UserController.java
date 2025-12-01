package com.jobportal.jobportal_api.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.jobportal_api.entity.User;
import com.jobportal.jobportal_api.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Autowired
    private UserService userService;

 // Get all users
    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            logger.error("Error fetching users: {}", e.getMessage());
            return ResponseEntity.status(500).body("Could not fetch users");
        }
    }

    
    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
    	
        if(user.isPresent())
        {
        	return ResponseEntity.ok(user.get());
        }
        else
        {
        	logger.error("User not found with id {})", id);
        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            logger.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    // Delete a role by ID 
    @DeleteMapping("/roles/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId) {
        try {
            userService.deleteRole(roleId);
            return ResponseEntity.ok("Role deleted successfully");
        } catch (RuntimeException e) {
            logger.error("Error deleting role: {}", e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}