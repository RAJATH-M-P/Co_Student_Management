package com.example.demo;
import jakarta.persistence.*; import lombok.*; import java.time.LocalDate;
@Entity @Table(name="attendance", uniqueConstraints=@UniqueConstraint(columnNames={"studentId","attendanceDate"})) @Data @NoArgsConstructor
public class Attendance { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @Column(nullable=false) private Long studentId; @Column(nullable=false) private LocalDate attendanceDate=LocalDate.now(); @Column(nullable=false) private String status="Present"; }
