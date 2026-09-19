package com.schoolmanagment.coreservice.attendance.repository;

import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
}
