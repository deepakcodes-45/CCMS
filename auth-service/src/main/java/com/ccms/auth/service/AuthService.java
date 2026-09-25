package com.ccms.auth.service;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ccms.auth.dto.AdminRegistrationRequest;
import com.ccms.auth.dto.AuthResponse;
import com.ccms.auth.dto.CustomerRegistrationRequest;
import com.ccms.auth.dto.LoginRequest;
import com.ccms.auth.dto.UserResponse;
import com.ccms.auth.entity.AppRole;
import com.ccms.auth.entity.AppUser;
import com.ccms.auth.enums.RoleName;
import com.ccms.auth.exception.DuplicateResourceException;
import com.ccms.auth.exception.InvalidCredentialsException;
import com.ccms.auth.exception.ResourceNotFoundException;
import com.ccms.auth.repository.AppRoleRepository;
import com.ccms.auth.repository.AppUserRepository;
import com.ccms.auth.repository.CustomerRepository;

@Service
@Transactional
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository appUserRepository,
            AppRoleRepository appRoleRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse registerAdmin(AdminRegistrationRequest request) {
        if (appUserRepository.existsByRoles_RoleName(RoleName.ADMIN)) {
            throw new DuplicateResourceException(
                    "An admin account already exists."
            );
        }

        String username = normalizeUsername(request.username());
        ensureUsernameIsAvailable(username);

        AppRole adminRole = getRole(RoleName.ADMIN);

        AppUser savedUser = createUser(
                username,
                request.password(),
                null,
                adminRole
        );

        return toUserResponse(savedUser, RoleName.ADMIN);
    }

    public UserResponse registerCustomer(CustomerRegistrationRequest request) {
        String username = normalizeUsername(request.username());
        ensureUsernameIsAvailable(username);

        if (!customerRepository.existsById(request.customerId())) {
            throw new ResourceNotFoundException(
                    "Customer not found: " + request.customerId()
            );
        }

        if (appUserRepository.existsByCustomerId(request.customerId())) {
            throw new DuplicateResourceException(
                    "This customer already has a login account."
            );
        }

        AppRole customerRole = getRole(RoleName.CUSTOMER);

        AppUser savedUser = createUser(
                username,
                request.password(),
                request.customerId(),
                customerRole
        );

        return toUserResponse(savedUser, RoleName.CUSTOMER);
    }

    public AuthResponse login(LoginRequest request) {
        String username = normalizeUsername(request.username());

        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid username or password."
                        )
                );

        if (!Integer.valueOf(1).equals(user.getEnabled())
                || !passwordEncoder.matches(
                        request.password(),
                        user.getPasswordHash()
                )) {
            throw new InvalidCredentialsException(
                    "Invalid username or password."
            );
        }

        RoleName role = user.getRoles().stream()
                .map(AppRole::getRoleName)
                .findFirst()
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid username or password."
                        )
                );

        String token = jwtService.generateToken(user, role);

        return new AuthResponse(
                user.getUserId(),
                user.getUsername(),
                role,
                user.getCustomerId(),
                token
        );
    }

    private AppUser createUser(
            String username,
            String rawPassword,
            Long customerId,
            AppRole role
    ) {
        AppUser user = new AppUser();

        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setEnabled(1);
        user.setCustomerId(customerId);
        user.setRoles(new HashSet<>(Set.of(role)));

        return appUserRepository.save(user);
    }

    private AppRole getRole(RoleName roleName) {
        return appRoleRepository.findByRoleName(roleName)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Role not found: " + roleName
                        )
                );
    }

    private void ensureUsernameIsAvailable(String username) {
        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException(
                    "Username is already in use."
            );
        }
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toUserResponse(AppUser user, RoleName role) {
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                role,
                user.getCustomerId(),
                Integer.valueOf(1).equals(user.getEnabled())
        );
    }
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String username) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user no longer exists."
                        )
                );

        RoleName role = user.getRoles().stream()
                .map(AppRole::getRoleName)
                .findFirst()
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user has no role."
                        )
                );

        return toUserResponse(user, role);
    }
}