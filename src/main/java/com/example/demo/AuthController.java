
package com.example.demo;

import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth")
class AuthController {

    // Repository and Security Variables
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    // Constructor Injection
    AuthController(
            UserRepository users,
            PasswordEncoder encoder,
            JwtService jwt) {

        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    // Credentials Record
    record Credentials(String username, String password) {
    }

    // =====================================
    // 1. REGISTER API
    // =====================================

    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody Credentials c) {

        // Validate Username and Password
        if (c.username() == null || c.username().isBlank()
                || c.password() == null
                || c.password().length() < 8) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error",
                            "Username is required and password must contain at least 8 characters"
                    ));
        }

        // Check Whether Username Already Exists
        if (users.findByUsername(c.username()).isPresent()) {

            return ResponseEntity.status(409)
                    .body(Map.of("error", "Username already exists"));
        }

        // Create New Admin User
        AppUser u = new AppUser();

        u.setUsername(c.username().trim());
        u.setPassword(encoder.encode(c.password()));
        u.setRole("ADMIN");

        // Save User in Database
        users.save(u);

        return ResponseEntity.ok(
                Map.of("message", "Admin account created. You can now log in.")
        );
    }

    // =====================================
    // 2. LOGIN API
    // =====================================

    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody Credentials c) {

        // Find User by Username
        AppUser u = users.findByUsername(c.username())
                .orElse(null);

        // Validate Username and Password
        if (u == null || !encoder.matches(c.password(), u.getPassword())) {

            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid username or password"));
        }

        // Generate JWT Token and Return User Details
        return ResponseEntity.ok(
                Map.of(
                        "token", jwt.generate(u.getUsername(), u.getRole()),
                        "username", u.getUsername(),
                        "role", u.getRole()
                )
        );
    }
}
