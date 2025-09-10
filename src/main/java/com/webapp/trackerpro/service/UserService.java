package com.webapp.trackerpro.service;

import com.webapp.trackerpro.dto.LoginDto;
import com.webapp.trackerpro.dto.UserRegistrationDto;
import com.webapp.trackerpro.dto.UserResponseDto;
import com.webapp.trackerpro.exception.BusinessException;
import com.webapp.trackerpro.mapper.UserMapper;
import com.webapp.trackerpro.model.Role;
import com.webapp.trackerpro.model.User;
import com.webapp.trackerpro.model.UserStatus;
import com.webapp.trackerpro.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        logger.info("Registering new user with email: {}", registrationDto.getEmail());

        // Validate passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        // Check if mobile already exists
        if (registrationDto.getMobileNo() != null && userRepository.existsByMobile(registrationDto.getMobileNo())) {
            throw new BusinessException("Mobile number already exists");
        }

        // Create new user
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setMobile(registrationDto.getMobileNo());
        user.setRole(Role.fromString(registrationDto.getRoleCategory()));
        user.setStatus(UserStatus.PENDING);

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return userMapper.toResponseDto(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto authenticateUser(LoginDto loginDto) {
        logger.info("Authenticating user with email: {}", loginDto.getEmail());

        Optional<User> userOptional = userRepository.findByEmail(loginDto.getEmail());
        if (userOptional.isEmpty()) {
            throw new BusinessException("Invalid email or password");
        }

        User user = userOptional.get();

        // Check if password matches
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BusinessException("Invalid email or password");
        }

        // Check if user is active (except for admin)
        if (user.getRole() != Role.ADMIN && user.getStatus() != UserStatus.ACTIVE) {
            String message = switch (user.getStatus()) {
                case PENDING -> "Your account is pending approval. Please contact administrator.";
                case INACTIVE -> "Your account has been deactivated. Please contact administrator.";
                case REJECTED -> "Your account registration was rejected. Please contact administrator.";
                default -> "Account access denied.";
            };
            throw new BusinessException(message);
        }

        logger.info("User authenticated successfully: {}", user.getEmail());
        return userMapper.toResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getPendingRegistrations() {
        logger.info("Fetching pending registrations");
        List<User> pendingUsers = userRepository.findPendingRegistrations(UserStatus.PENDING);
        return pendingUsers.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto approveUser(Long userId) {
        logger.info("Approving user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (user.getStatus() != UserStatus.PENDING) {
            throw new BusinessException("Only pending users can be approved");
        }

        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        logger.info("User approved successfully: {}", savedUser.getEmail());
        return userMapper.toResponseDto(savedUser);
    }

    public void rejectUser(Long userId) {
        logger.info("Rejecting user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (user.getStatus() != UserStatus.PENDING) {
            throw new BusinessException("Only pending users can be rejected");
        }

        user.setStatus(UserStatus.REJECTED);
        userRepository.save(user);

        logger.info("User rejected successfully: {}", user.getEmail());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        logger.info("Fetching all non-admin users");
        List<User> users = userRepository.findAllNonAdminUsers();
        return users.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByRole(Role role) {
        logger.info("Fetching users by role: {}", role);
        List<User> users = userRepository.findByRole(role);
        return users.stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUser(Long userId, UserRegistrationDto updateDto) {
        logger.info("Updating user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(updateDto.getEmail()) && 
            userRepository.existsByEmail(updateDto.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        // Update user fields
        user.setFirstName(updateDto.getFirstName());
        user.setLastName(updateDto.getLastName());
        user.setEmail(updateDto.getEmail());
        user.setMobile(updateDto.getMobileNo());
        
        if (updateDto.getRoleCategory() != null) {
            user.setRole(Role.fromString(updateDto.getRoleCategory()));
        }

        // Update password if provided
        if (updateDto.getPassword() != null && !updateDto.getPassword().isEmpty()) {
            if (!updateDto.getPassword().equals(updateDto.getConfirmPassword())) {
                throw new BusinessException("Passwords do not match");
            }
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }

        User savedUser = userRepository.save(user);
        logger.info("User updated successfully: {}", savedUser.getEmail());

        return userMapper.toResponseDto(savedUser);
    }

    public void deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new BusinessException("Cannot delete admin user");
        }

        userRepository.delete(user);
        logger.info("User deleted successfully: {}", user.getEmail());
    }

    public void toggleUserStatus(Long userId) {
        logger.info("Toggling status for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new BusinessException("Cannot modify admin user status");
        }

        UserStatus newStatus = (user.getStatus() == UserStatus.ACTIVE) ? 
                               UserStatus.INACTIVE : UserStatus.ACTIVE;
        user.setStatus(newStatus);
        
        userRepository.save(user);
        logger.info("User status updated to {} for: {}", newStatus, user.getEmail());
    }

    @Transactional(readOnly = true)
    public Long getPendingRegistrationsCount() {
        return userRepository.countPendingRegistrations();
    }

    @Transactional(readOnly = true)
    public Long getActiveUsersCountByRole(Role role) {
        return userRepository.countActiveUsersByRole(role);
    }
}