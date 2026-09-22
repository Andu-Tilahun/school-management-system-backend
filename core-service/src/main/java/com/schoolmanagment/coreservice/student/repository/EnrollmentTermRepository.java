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

    Optional<EnrollmentTerm> findByEnrollmentIdAndStatus(
            UUID enrollmentId, EnrollmentTermStatus status);

    @Query("""
            SELECT et FROM EnrollmentTerm et
            WHERE et.enrollment.id = :enrollmentId
            ORDER BY et.term.sequenceOrder ASC
            """)
    List<EnrollmentTerm> findByEnrollmentIdOrderByTermSequenceOrderAsc(
            @Param("enrollmentId") UUID enrollmentId);

    List<EnrollmentTerm> findAllByStatus(EnrollmentTermStatus status);

}
