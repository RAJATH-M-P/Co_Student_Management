
package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    // Find Fee Records Using Student ID
    List<Fee> findByStudentId(Long id);

}
