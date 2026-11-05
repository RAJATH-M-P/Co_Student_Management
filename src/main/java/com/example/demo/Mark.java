
package com.example.demo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marks")
@Data
@NoArgsConstructor
public class Mark {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Student ID
    private Long studentId;

    // Subject Name
    private String subject;

    // Exam Type
    private String examType;

    // Marks Obtained
    private Double marksObtained;

    // Maximum Marks (Default: 100.0)
    private Double maxMarks = 100.0;
}
