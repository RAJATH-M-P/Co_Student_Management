package com.example.demo;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface FeeRepository extends JpaRepository<Fee,Long> { List<Fee> findByStudentId(Long id); }
