package com.ccms.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.auth.entity.AppUser;
import com.ccms.auth.enums.RoleName;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByCustomerId(Long customerId);

    boolean existsByRoles_RoleName(RoleName roleName);
}