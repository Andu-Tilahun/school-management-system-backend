package com.schoolmanagment.coreservice.exam.entity;


import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.exam.enums.MarkType;
import com.schoolmanagment.coreservice.exam.enums.PassFailStatus;
import com.schoolmanagment.coreservice.student.entity.EnrollmentTerm;
import com.schoolmanagment.coreservice.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
@Entity
@Table(name = "tbl_subject_totals")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectTotal extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_term_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private EnrollmentTerm enrollmentTerm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Subject subject;

    @Column(name = "total_mark", nullable = false)
    private Double totalMark;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PassFailStatus status;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
}
