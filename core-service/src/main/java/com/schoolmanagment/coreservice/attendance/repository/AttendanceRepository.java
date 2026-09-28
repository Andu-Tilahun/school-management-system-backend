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

    List<Attendance> findByEnrollmentTerm_IdAndActiveTrue(UUID enrollmentTermId);

    @Query("""
            SELECT a FROM Attendance a
            WHERE a.enrollmentTerm.enrollment.id = :enrollmentId
              AND a.penaltyTrigger = :trigger
              AND a.active = true
            """)
    List<Attendance> findActiveByEnrollmentAndTrigger(
            @Param("enrollmentId") UUID enrollmentId,
            @Param("trigger") PenaltyTrigger trigger);

    List<Attendance> findByEnrollmentTerm_Enrollment_IdAndPenaltyTrigger(
            UUID enrollmentId, PenaltyTrigger trigger);
}
