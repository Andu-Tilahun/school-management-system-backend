package com.schoolmanagment.coreservice.student.repository;

import com.schoolmanagment.coreservice.student.entity.EmergencyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, UUID> {

    Optional<EmergencyContact> findBySchoolIdAndEmail(UUID schoolId, String email);

    Optional<EmergencyContact> findBySchoolIdAndEmailAndActiveTrue(UUID schoolId, String email);

    Optional<EmergencyContact> findByIdAndSchoolId(UUID id, UUID schoolId);
}
