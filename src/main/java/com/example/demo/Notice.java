package com.example.demo;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDateTime;
@Entity @Table(name="notices") @Data @NoArgsConstructor
public class Notice { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private String title; @Column(length=3000) private String message; private LocalDateTime createdAt=LocalDateTime.now(); }
