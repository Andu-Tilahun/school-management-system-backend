package com.schoolmanagment.coreservice.student.repository;

import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.student.enums.EnrollmentStatus;
import com.schoolmanagment.coreservice.student.enums.EnrollmentTermStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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

    @Query("""
            SELECT et FROM EnrollmentTerm et
            JOIN et.enrollment e
            WHERE e.student.id = :studentId
              AND e.schoolId = :schoolId
              AND e.active = true
              AND e.status = :enrollmentStatus
              AND et.active = true
              AND et.status = :status
            """)
    List<EnrollmentTerm> findActiveByStudentIdAndSchoolId(
            @Param("studentId") UUID studentId,
            @Param("schoolId") UUID schoolId,
            @Param("enrollmentStatus") EnrollmentStatus enrollmentStatus,
            @Param("status") EnrollmentTermStatus status);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE EnrollmentTerm et
            SET et.status = :completed
            WHERE et.term.id IN :termIds
              AND et.status = :activeStatus
              AND et.active = true
            """)
    int completeActiveByTermIds(
            @Param("termIds") Collection<UUID> termIds,
            @Param("completed") EnrollmentTermStatus completed,
            @Param("activeStatus") EnrollmentTermStatus activeStatus);

    @Query("""
            SELECT et FROM EnrollmentTerm et
            JOIN FETCH et.enrollment e
            JOIN FETCH e.student s
            JOIN FETCH et.term t
            JOIN FETCH t.academicYear
            WHERE et.active = true
              AND (:enrollmentId IS NULL OR e.id = :enrollmentId)
              AND (:studentId IS NULL OR s.id = :studentId)
              AND (:schoolId IS NULL OR et.schoolId = :schoolId)
            ORDER BY t.startDate ASC, t.semester ASC
            """)
    List<EnrollmentTerm> findActiveForMarkLookup(
            @Param("enrollmentId") UUID enrollmentId,
            @Param("studentId") UUID studentId,
            @Param("schoolId") UUID schoolId);

}
