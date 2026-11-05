
package com.example.demo;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
@Data
@NoArgsConstructor
public class Notice {

    // Primary Key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Notice Title
    private String title;

    // Notice Message
    @Column(length = 3000)
    private String message;

    // Notice Creation Date and Time
    private LocalDateTime createdAt = LocalDateTime.now();
}
