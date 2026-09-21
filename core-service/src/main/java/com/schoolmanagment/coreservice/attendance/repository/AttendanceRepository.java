package com.schoolmanagment.coreservice.attendance.repository;

import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, UUID>, JpaSpecificationExecutor<Attendance> {

    Page<Attendance> findByActiveTrue(Pageable pageable);

    Optional<Attendance> findByIdAndActiveTrue(UUID id);

    List<Attendance> findByEnrollment_IdAndActiveTrue(UUID enrollmentId);

    List<Attendance> findByAcademicYear_IdAndActiveTrue(UUID academicYearId);

    // Active attendance rows for a given enrollment + trigger — this is the
    // count reconciliation checks against. "Active" excludes soft-deleted
    // (teacher-removed) records, which is exactly what makes the
    // reconciliation logic self-correcting.
    @Query("""
            SELECT a FROM Attendance a
            WHERE a.enrollment.id = :enrollmentId
              AND a.penaltyTrigger = :trigger
              AND a.active = true
            """)
    List<Attendance> findActiveByEnrollmentAndTrigger(
            @Param("enrollmentId") UUID enrollmentId,
            @Param("trigger") PenaltyTrigger trigger);

    // Convenience for reporting/admin screens — full history including inactive.
    List<Attendance> findByEnrollmentIdAndPenaltyTrigger(
            UUID enrollmentId, PenaltyTrigger trigger);
}
