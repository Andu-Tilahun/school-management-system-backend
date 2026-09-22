package com.schoolmanagment.coreservice.exam.repository;

import com.schoolmanagment.coreservice.exam.entity.SubjectTotal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectTotalRepository extends JpaRepository<SubjectTotal, UUID> {

    Optional<SubjectTotal> findByEnrollmentTermIdAndSubjectId(
            UUID enrollmentTermId, UUID subjectId);

    List<SubjectTotal> findByEnrollmentTermIdAndActiveTrue(UUID enrollmentTermId);
}
