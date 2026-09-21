package com.schoolmanagment.coreservice.attendance.entity;

import com.schoolmanagment.coreservice.academicyear.entity.AcademicYear;
import com.schoolmanagment.coreservice.attendance.enums.AttendanceStatus;
import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.penalty.enums.PenaltyTrigger;
import com.schoolmanagment.coreservice.student.entity.Enrollment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tbl_attendances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Attendance extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_trigger", nullable = false, length = 40)
    private PenaltyTrigger penaltyTrigger;

    @Column(name = "date_occurred", nullable = false)
    private LocalDate dateOccurred;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AcademicYear academicYear;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
