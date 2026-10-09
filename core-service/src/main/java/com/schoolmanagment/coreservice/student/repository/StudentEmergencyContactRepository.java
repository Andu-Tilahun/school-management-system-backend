package com.schoolmanagment.coreservice.student.repository;

import com.schoolmanagment.coreservice.student.entity.Student;
import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentEmergencyContactRepository extends JpaRepository<StudentEmergencyContact, UUID> {

    List<StudentEmergencyContact> findByStudent_IdAndActiveTrue(UUID studentId);

    Optional<StudentEmergencyContact> findByStudent_IdAndEmergencyContact_Id(UUID studentId, UUID emergencyContactId);

    Optional<StudentEmergencyContact> findFirstByEmergencyContact_IdAndActiveTrueAndIsPrimaryTrue(UUID emergencyContactId);

    boolean existsByStudent_IdAndEmergencyContact_IdAndActiveTrue(UUID studentId, UUID emergencyContactId);

    @Query("""
            SELECT DISTINCT s
            FROM StudentEmergencyContact link
            JOIN link.student s
            WHERE link.emergencyContact.id = :emergencyContactId
              AND link.active = true
              AND link.emergencyContact.active = true
              AND s.active = true
            ORDER BY s.firstName ASC, s.lastName ASC
            """)
    List<Student> findActiveStudentsByEmergencyContactId(
            @Param("emergencyContactId") UUID emergencyContactId);
}
