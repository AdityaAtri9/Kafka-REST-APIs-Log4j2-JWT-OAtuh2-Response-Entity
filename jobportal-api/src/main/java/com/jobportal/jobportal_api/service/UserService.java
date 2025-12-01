package com.jobportal.jobportal_api.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.jobportal_api.entity.Role;
import com.jobportal.jobportal_api.entity.User;
import com.jobportal.jobportal_api.repository.RoleRepository;
import com.jobportal.jobportal_api.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    public Optional<User> getUserById(Long id) {
        logger.info("Fetching user with id: {}", id);
        return userRepository.findById(id);
    }

    public User updateUser(Long id, User userDetails) {
        logger.info("Updating user with id: {}", id);

        return userRepository.findById(id).map(user -> {
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
           
            if(userDetails.getPassword() != null && !userDetails.getPassword().isEmpty())
            {
            	user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            user.setRole(userDetails.getRole());  // fixed
            User updated = userRepository.save(user);
            logger.info("User updated successfully: {}", updated.getUsername());
            return updated;
        }).orElseThrow(() -> {
            logger.error("User not found with id: {}", id);
            return new RuntimeException("User not found");
        });
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            logger.error("User not found with id: {}", id);
            return new RuntimeException("User not found");
        });

        // Remove relationships first to avoid FK constraint
        user.getRole().clear();
        userRepository.save(user);

        userRepository.delete(user);
        logger.info("User deleted successfully: {}", user.getUsername());
    }

    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Remove role from all users before deleting
        userRepository.findAll().forEach(user -> {
            if(user.getRole().contains(role)) {
                user.getRole().remove(role);
                userRepository.save(user);
            }
        });

        roleRepository.delete(role);
        logger.info("Role deleted successfully: {}", role.getName());
    }

	public List<User> getAllUsers() {
		logger.info("Fetching all users.");
		return userRepository.findAll();
	}
}
