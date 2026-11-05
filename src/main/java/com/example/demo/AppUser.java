
package com.example.demo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
public class AppUser {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username
    @Column(nullable = false, unique = true)
    private String username;

    // Password
    @Column(nullable = false)
    private String password;

    // User Role
    private String role = "ADMIN";

    // Student ID
    private Long studentId;
}
