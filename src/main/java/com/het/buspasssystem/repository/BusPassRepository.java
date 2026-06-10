package com.het.buspasssystem.repository;

import java.util.List;
import com.het.buspasssystem.entity.BusPass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BusPassRepository extends JpaRepository<BusPass, Integer> {
    List<BusPass> findByEnrollmentNo(String enrollmentNo);

    long count();
    long countByStatus(String status);

    List<BusPass> findByEnrollmentNoContaining(String enrollmentNo);

    Optional<BusPass> findTopByStudentNameOrderByAppliedDateDesc(String studentName);

    long countByStudentName(String studentName);

    long countByStudentNameAndStatus(String studentName, String status);

    List<BusPass> findByStudentNameOrderByAppliedDateDesc(String studentName);
}
