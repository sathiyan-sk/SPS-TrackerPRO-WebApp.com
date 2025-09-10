package com.webapp.trackerpro.controller;

import com.webapp.trackerpro.dto.LoginDto;
import com.webapp.trackerpro.dto.UserRegistrationDto;
import com.webapp.trackerpro.dto.UserResponseDto;
import com.webapp.trackerpro.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        logger.info("Registration request received for email: {}", registrationDto.getEmail());
        
        try {
            UserResponseDto userResponse = userService.registerUser(registrationDto);
            logger.info("User registered successfully: {}", userResponse.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Registration successful! Your account is pending approval.",
                "user", userResponse
            ));
        } catch (Exception e) {
            logger.error("Registration failed for email: {}, error: {}", registrationDto.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDto loginDto) {
        logger.info("Login request received for email: {}", loginDto.getEmail());
        
        try {
            UserResponseDto userResponse = userService.authenticateUser(loginDto);
            logger.info("User logged in successfully: {}", userResponse.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Login successful!",
                "user", userResponse
            ));
        } catch (Exception e) {
            logger.error("Login failed for email: {}, error: {}", loginDto.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@Valid @RequestBody LoginDto loginDto) {
        logger.info("Admin login request received for email: {}", loginDto.getEmail());
        
        try {
            UserResponseDto userResponse = userService.authenticateUser(loginDto);
            
            // Verify it's an admin user
            if (!"ADMIN".equals(userResponse.getRole().name())) {
                logger.warn("Non-admin user attempted admin login: {}", loginDto.getEmail());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Access denied. Admin privileges required."
                ));
            }
            
            logger.info("Admin logged in successfully: {}", userResponse.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Admin login successful!",
                "user", userResponse,
                "redirectUrl", "/adminDashboard.html"
            ));
        } catch (Exception e) {
            logger.error("Admin login failed for email: {}, error: {}", loginDto.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String emailOrMobile = request.get("emailOrMobile");
        logger.info("Forgot password request received for: {}", emailOrMobile);
        
        // For now, just return a success message
        // In a real application, you would send a password reset email/SMS
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "If the email/mobile exists in our system, you will receive password reset instructions."
        ));
    }
}