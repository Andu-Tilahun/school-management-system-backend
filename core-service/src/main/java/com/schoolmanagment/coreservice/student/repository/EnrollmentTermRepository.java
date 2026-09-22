package com.schoolmanagment.coreservice.student.repository;

import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentTermRepository extends JpaRepository<EnrollmentTerm, UUID> {

    boolean existsByEnrollmentIdAndTermId(UUID enrollmentId, UUID termId);

    boolean existsByEnrollment_SchoolIdAndEnrollment_Student_IdAndTerm_Id(
            UUID schoolId,
            UUID studentId,
            UUID termId
    );

    boolean existsByEnrollment_SchoolIdAndEnrollment_Student_IdAndTerm_IdAndEnrollment_IdNot(
            UUID schoolId,
            UUID studentId,
            UUID termId,
            UUID enrollmentId
    );

    Optional<EnrollmentTerm> findByEnrollmentIdAndStatus(
            UUID enrollmentId, EnrollmentTermStatus status);

    @Query("""
            SELECT et FROM EnrollmentTerm et
            WHERE et.enrollment.id = :enrollmentId
            ORDER BY et.term.startDate ASC, et.term.semester ASC
            """)
    List<EnrollmentTerm> findByEnrollmentIdOrderByTermSequenceOrderAsc(
            @Param("enrollmentId") UUID enrollmentId);

    List<EnrollmentTerm> findAllByStatus(EnrollmentTermStatus status);

}
