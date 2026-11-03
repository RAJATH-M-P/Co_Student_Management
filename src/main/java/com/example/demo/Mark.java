package com.example.demo;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="marks") @Data @NoArgsConstructor
public class Mark { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; private Long studentId; private String subject; private String examType; private Double marksObtained; private Double maxMarks=100.0; }
