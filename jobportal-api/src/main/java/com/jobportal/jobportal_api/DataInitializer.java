package com.jobportal.jobportal_api;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.jobportal.jobportal_api.entity.Role;
import com.jobportal.jobportal_api.entity.User;
import com.jobportal.jobportal_api.repository.RoleRepository;
import com.jobportal.jobportal_api.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LogManager.getLogger(DataInitializer.class);

    @Autowired	
    private RoleRepository roleRepo;

	@Autowired
    private UserRepository userRepo;

	@Autowired
    private PasswordEncoder passwordEncoder;



    public DataInitializer(RoleRepository roleRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        logger.info("Running DataInitializer");

        Role admin = roleRepo.findByName("ROLE_ADMIN").orElseGet(() -> roleRepo.save(new Role(null,"ROLE_ADMIN")));
        Role user = roleRepo.findByName("ROLE_USER").orElseGet(() -> roleRepo.save(new Role(null,"ROLE_USER")));

        if (!userRepo.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@gmail.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.getRole().add(admin);
            adminUser.getRole().add(user);
            userRepo.save(adminUser);
            logger.info("Created default admin user: admin / admin123");
        } else {
            logger.info("Admin user already exists");
        }
    }
}

