package com.webapp.trackerpro.config;

import com.webapp.trackerpro.model.Role;
import com.webapp.trackerpro.model.User;
import com.webapp.trackerpro.model.UserStatus;
import com.webapp.trackerpro.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        createDefaultAdminUser();
        createSampleUsers();
    }

    private void createDefaultAdminUser() {
        logger.info("Checking for default admin user...");
        
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setMobile("9999999999");
            adminUser.setRole(Role.ADMIN);
            adminUser.setStatus(UserStatus.ACTIVE);

            userRepository.save(adminUser);
            logger.info("Default admin user created with email: {}", adminEmail);
        } else {
            logger.info("Default admin user already exists");
        }
    }

    private void createSampleUsers() {
        logger.info("Creating sample users for testing...");

        // Sample pending registrations
        createSampleUserIfNotExists("John", "Smith", "john.s@example.com", "password123", "9876543210", Role.STUDENT, UserStatus.PENDING);
        createSampleUserIfNotExists("Sarah", "Johnson", "sarah.j@example.com", "password123", "9876543211", Role.FACULTY, UserStatus.PENDING);
        createSampleUserIfNotExists("Mike", "Williams", "mike.w@example.com", "password123", "9876543212", Role.HR, UserStatus.PENDING);

        // Sample active users
        createSampleUserIfNotExists("Alice", "Brown", "alice.b@example.com", "password123", "9876543213", Role.STUDENT, UserStatus.ACTIVE);
        createSampleUserIfNotExists("Robert", "Davis", "robert.d@example.com", "password123", "9876543214", Role.FACULTY, UserStatus.ACTIVE);
        createSampleUserIfNotExists("Emily", "Wilson", "emily.w@example.com", "password123", "9876543215", Role.HR, UserStatus.ACTIVE);

        logger.info("Sample users created successfully");
    }

    private void createSampleUserIfNotExists(String firstName, String lastName, String email, 
                                           String password, String mobile, Role role, UserStatus status) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setMobile(mobile);
            user.setRole(role);
            user.setStatus(status);

            userRepository.save(user);
            logger.debug("Sample user created: {} - {}", email, status);
        }
    }
}