package com.example.demo;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDate;
@Entity @Table(name="students") @Data @NoArgsConstructor
public class Student {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false) private String name;
 @Column(nullable=false, unique=true) private String email;
 private String phone; private String department; private String yearOfStudy;
 private LocalDate enrollmentDate=LocalDate.now(); private String status="Active";
}
