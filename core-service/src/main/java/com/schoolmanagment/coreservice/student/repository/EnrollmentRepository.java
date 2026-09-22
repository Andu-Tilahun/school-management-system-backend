package com.schoolmanagment.coreservice.student.repository;

import com.schoolmanagment.coreservice.student.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID>, JpaSpecificationExecutor<Enrollment> {

    List<Enrollment> findByStudent_IdAndActiveTrue(UUID studentId);

    Optional<Enrollment> findByIdAndActiveTrue(UUID id);

    Optional<Enrollment> findBySchoolIdAndStudent_IdAndActiveTrue(UUID schoolId, UUID studentId);
}
