package com.schoolmanagment.coreservice.student.repository;

import com.schoolmanagment.coreservice.student.entity.StudentEmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentEmergencyContactRepository extends JpaRepository<StudentEmergencyContact, UUID> {

    List<StudentEmergencyContact> findByStudent_IdAndActiveTrue(UUID studentId);

    Optional<StudentEmergencyContact> findByStudent_IdAndEmergencyContact_Id(UUID studentId, UUID emergencyContactId);

    boolean existsByStudent_IdAndEmergencyContact_IdAndActiveTrue(UUID studentId, UUID emergencyContactId);
}
