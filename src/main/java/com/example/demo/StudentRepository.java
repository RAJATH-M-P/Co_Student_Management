package com.example.demo;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface StudentRepository extends JpaRepository<Student,Long> {
     List<Student> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(String n,String e,String d);
     }
