package com.ccms.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ccms.auth.entity.AppRole;
import com.ccms.auth.enums.RoleName;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

    Optional<AppRole> findByRoleName(RoleName roleName);
}