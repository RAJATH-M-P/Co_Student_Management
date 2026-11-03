package com.example.demo;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDate;
@Entity @Table(name="fees") @Data @NoArgsConstructor
public class Fee { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long studentId; private String description; private Double amount; private Double paidAmount=0.0; private LocalDate dueDate; private String status="Pending"; }
