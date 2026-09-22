package com.schoolmanagment.coreservice.academicyear.entity;

import com.schoolmanagment.coreservice.academicyear.enums.Semester;
import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tbl_terms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Term extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AcademicYear academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Semester semester;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
