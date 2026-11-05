
package com.example.demo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "fees")
@Data
@NoArgsConstructor
public class Fee {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student ID
    private Long studentId;

    // Fee Description
    private String description;

    // Total Fee Amount
    private Double amount;

    // Paid Amount (Default: 0.0)
    private Double paidAmount = 0.0;

    // Fee Due Date
    private LocalDate dueDate;

    // Fee Payment Status (Default: Pending)
    private String status = "Pending";
}
