package com.schoolmanagment.coreservice.penalty.entity;

import com.schoolmanagment.coreservice.attendance.entity.Attendance;
import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tbl_penalty_source_attendances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltySourceAttendance extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_id", nullable = false)
    private Penalty penalty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id", nullable = false)
    private Attendance attendance;
}
