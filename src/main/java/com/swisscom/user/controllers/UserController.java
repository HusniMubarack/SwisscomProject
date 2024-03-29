package com.swisscom.user.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.swisscom.user.models.User;
import com.swisscom.user.repositories.UserRepository;
import com.swisscom.user.services.OpaService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserRepository userRepository;
    private OpaService opaService;

    public UserController(UserRepository userRepository, OpaService opaService) {
        this.userRepository = userRepository;
        this.opaService = opaService;
    }

    // Endpoint to retrieve all users
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        String userRole = getCurrentUserRole();

        // Check if the user is authenticated
        if (userRole != null) {
            if (!opaService.isAllowed("read", userRole, true)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to read users.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please authenticate to access this resource.");
        }
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    // Endpoint to create a new user
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Get the current authentication object
        String userRole = getCurrentUserRole();

        // Check if the user is authenticated
        if (userRole != null) {
            if (!opaService.isAllowed("create", userRole, true)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to create a new user.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please authenticate to create a new user.");
        }

        User newUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                String authorityName = authority.getAuthority();
                if ("ROLE_ADMIN".equals(authorityName)) {
                    return "admin";
                } else if ("ROLE_USER".equals(authorityName)) {
                    return "user";
                }
            }
        }

        return null;
    }
}
