package com.schoolmanagment.coreservice.subject.entity;

import com.schoolmanagment.coreservice.subject.enums.SubjectStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "subjects",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_subject_name_grade",
                columnNames = {"subject_name", "grade_level"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "subject_code", nullable = false, unique = true)
    private String subjectCode;
    @Column(name = "subject_name", nullable = false)
    private String subjectName;
    @Column(name = "credit_hours", nullable = false)
    private Integer creditHours;
    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;
   @Enumerated(EnumType.STRING)
   @Column(name = "status", nullable = false)
    private SubjectStatus status;
}
