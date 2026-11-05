
package com.example.demo;

// Imports
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
public class Student {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student Name
    @Column(nullable = false)
    private String name;

    // Student Email
    @Column(nullable = false, unique = true)
    private String email;

    // Student Phone Number
    private String phone;

    // Department
    private String department;

    // Year of Study
    private String yearOfStudy;

    // Enrollment Date (Default: Current Date)
    private LocalDate enrollmentDate = LocalDate.now();

    // Student Status (Default: Active)
    private String status = "Active";
}
