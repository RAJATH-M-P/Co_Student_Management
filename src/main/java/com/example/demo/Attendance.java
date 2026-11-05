
package com.example.demo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "attendance",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"studentId", "attendanceDate"}
    )
)
@Data
@NoArgsConstructor
public class Attendance {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student ID
    @Column(nullable = false)
    private Long studentId;

    // Attendance Date
    @Column(nullable = false)
    private LocalDate attendanceDate = LocalDate.now();

    // Attendance Status
    @Column(nullable = false)
    private String status = "Present";
}
