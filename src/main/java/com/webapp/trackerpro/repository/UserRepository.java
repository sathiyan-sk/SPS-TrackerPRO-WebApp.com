package com.webapp.trackerpro.repository;

import com.webapp.trackerpro.model.Role;
import com.webapp.trackerpro.model.User;
import com.webapp.trackerpro.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    List<User> findByRole(Role role);

    List<User> findByStatus(UserStatus status);

    List<User> findByRoleAndStatus(Role role, UserStatus status);

    @Query("SELECT u FROM User u WHERE u.status = :status ORDER BY u.createdAt DESC")
    List<User> findPendingRegistrations(@Param("status") UserStatus status);

    @Query("SELECT u FROM User u WHERE u.role != 'ADMIN' ORDER BY u.createdAt DESC")
    List<User> findAllNonAdminUsers();

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = 'ACTIVE' ORDER BY u.firstName")
    Page<User> findActiveUsersByRole(@Param("role") Role role, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.status = 'ACTIVE'")
    Long countActiveUsersByRole(@Param("role") Role role);

    @Query("SELECT COUNT(u) FROM User u WHERE u.status = 'PENDING'")
    Long countPendingRegistrations();
}