package com.schoolmanagment.coreservice.exam.repository;

import com.schoolmanagment.coreservice.exam.entity.StudentMark;
import com.schoolmanagment.coreservice.exam.enums.MarkStatus;
import com.schoolmanagment.coreservice.exam.enums.MarkType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentMarkRepository extends JpaRepository<StudentMark, UUID> {

    List<StudentMark> findByEnrollmentTermIdAndSubjectIdAndStatusAndActiveTrue(
            UUID enrollmentTermId, UUID subjectId, MarkStatus status);

    Optional<StudentMark> findByEnrollmentTermIdAndSubjectIdAndType(
            UUID enrollmentTermId, UUID subjectId, MarkType type);

    List<StudentMark> findByEnrollmentTermIdAndActiveTrue(UUID enrollmentTermId);
}
