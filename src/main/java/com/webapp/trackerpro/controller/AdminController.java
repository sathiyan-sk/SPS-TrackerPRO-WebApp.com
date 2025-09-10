package com.webapp.trackerpro.controller;

import com.webapp.trackerpro.dto.UserResponseDto;
import com.webapp.trackerpro.model.Role;
import com.webapp.trackerpro.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/pending-registrations")
    public ResponseEntity<?> getPendingRegistrations() {
        logger.info("Fetching pending registrations");
        
        try {
            List<UserResponseDto> pendingUsers = userService.getPendingRegistrations();
            logger.info("Found {} pending registrations", pendingUsers.size());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", pendingUsers,
                "count", pendingUsers.size()
            ));
        } catch (Exception e) {
            logger.error("Error fetching pending registrations: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/approve-user/{userId}")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {
        logger.info("Approving user with ID: {}", userId);
        
        try {
            UserResponseDto approvedUser = userService.approveUser(userId);
            logger.info("User approved successfully: {}", approvedUser.getEmail());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User approved successfully!",
                "user", approvedUser
            ));
        } catch (Exception e) {
            logger.error("Error approving user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/reject-user/{userId}")
    public ResponseEntity<?> rejectUser(@PathVariable Long userId) {
        logger.info("Rejecting user with ID: {}", userId);
        
        try {
            userService.rejectUser(userId);
            logger.info("User rejected successfully with ID: {}", userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User rejected successfully!"
            ));
        } catch (Exception e) {
            logger.error("Error rejecting user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String role) {
        logger.info("Fetching users with role filter: {}", role);
        
        try {
            List<UserResponseDto> users;
            if (role != null && !role.isEmpty() && !"all".equalsIgnoreCase(role)) {
                Role roleEnum = Role.fromString(role);
                users = userService.getUsersByRole(roleEnum);
            } else {
                users = userService.getAllUsers();
            }
            
            logger.info("Found {} users", users.size());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", users,
                "count", users.size()
            ));
        } catch (Exception e) {
            logger.error("Error fetching users: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/toggle-user-status/{userId}")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        logger.info("Toggling status for user with ID: {}", userId);
        
        try {
            userService.toggleUserStatus(userId);
            logger.info("User status toggled successfully for ID: {}", userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User status updated successfully!"
            ));
        } catch (Exception e) {
            logger.error("Error toggling user status {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        
        try {
            userService.deleteUser(userId);
            logger.info("User deleted successfully with ID: {}", userId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User deleted successfully!"
            ));
        } catch (Exception e) {
            logger.error("Error deleting user {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/dashboard-stats")
    public ResponseEntity<?> getDashboardStats() {
        logger.info("Fetching dashboard statistics");
        
        try {
            Long totalStudents = userService.getActiveUsersCountByRole(Role.STUDENT);
            Long totalFaculty = userService.getActiveUsersCountByRole(Role.FACULTY);
            Long totalHR = userService.getActiveUsersCountByRole(Role.HR);
            Long pendingRequests = userService.getPendingRegistrationsCount();
            
            Map<String, Object> stats = Map.of(
                "totalStudents", totalStudents,
                "totalFaculty", totalFaculty,
                "totalHR", totalHR,
                "pendingRequests", pendingRequests,
                "activeBatches", 15, // Static data for now
                "totalUsers", totalStudents + totalFaculty + totalHR
            );
            
            logger.info("Dashboard stats: {}", stats);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", stats
            ));
        } catch (Exception e) {
            logger.error("Error fetching dashboard stats: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}