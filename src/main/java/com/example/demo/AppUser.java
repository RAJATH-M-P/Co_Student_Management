package com.example.demo;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="app_users") @Data @NoArgsConstructor
public class AppUser { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false,unique=true) private String username; @Column(nullable=false) private String password; private String role="ADMIN"; private Long studentId; }
