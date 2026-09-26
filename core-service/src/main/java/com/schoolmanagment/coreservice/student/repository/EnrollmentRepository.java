package com.schoolmanagment.coreservice.student.repository;

import com.schoolmanagment.coreservice.student.entity.Enrollment;
import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>, JpaSpecificationExecutor<Enrollment> {

    List<Enrollment> findByStudent_IdAndActiveTrue(UUID studentId);

    Optional<Enrollment> findByIdAndActiveTrue(UUID id);

    Optional<Enrollment> findBySchoolIdAndStudent_IdAndActiveTrue(UUID schoolId, UUID studentId);

    @Query("""
            SELECT DISTINCT s
            FROM Enrollment e
            JOIN e.student s
            WHERE e.classSection.id IN :classSectionIds
              AND e.active = true
              AND e.status = :status
              AND s.active = true
            ORDER BY s.firstName ASC, s.lastName ASC
            """)
    List<Student> findActiveStudentsByClassSectionIds(
            @Param("classSectionIds") Collection<UUID> classSectionIds,
            @Param("status") EnrollmentStatus status);
}
