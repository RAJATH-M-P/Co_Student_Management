package com.example.demo;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface MarkRepository extends JpaRepository<Mark,Long> { List<Mark> findByStudentId(Long id); }
