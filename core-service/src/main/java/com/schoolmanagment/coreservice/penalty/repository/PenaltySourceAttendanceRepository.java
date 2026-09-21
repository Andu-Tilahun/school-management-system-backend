package com.schoolmanagment.coreservice.penalty.repository;

import com.schoolmanagment.coreservice.penalty.entity.PenaltySourceAttendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PenaltySourceAttendanceRepository
        extends JpaRepository<PenaltySourceAttendance, UUID> {


    List<PenaltySourceAttendance> findByPenaltyId(UUID penaltyId);

    List<PenaltySourceAttendance> findByAttendanceId(UUID attendanceId);
}
