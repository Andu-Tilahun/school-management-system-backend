package com.schoolmanagment.coreservice.subject.entity;

import com.schoolmanagment.coreservice.auditable.SchoolAuditable;
import com.schoolmanagment.coreservice.grade.entity.Grade;
import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "tbl_subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Subject extends SchoolAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "subject_code", nullable = false)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Grade grade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubjectStatus status;
}
